﻿# 新增功能端到端测试（UTF-8 BOM）
$ErrorActionPreference = 'Stop'
$base = 'http://localhost:8080/api'

function Post-Json($url, $bodyObj, $headers) {
    $json = $bodyObj | ConvertTo-Json -Compress
    $resp = Invoke-WebRequest -Uri $url -Method Post -Body $json -ContentType 'application/json; charset=utf-8' -Headers $headers -UseBasicParsing
    return ($resp.Content | ConvertFrom-Json)
}
function Get-Url($url, $headers) {
    $resp = Invoke-WebRequest -Uri $url -Method Get -Headers $headers -UseBasicParsing
    return ($resp.Content | ConvertFrom-Json)
}
function Delete-Json($url, $bodyObj, $headers) {
    # PowerShell 5.1 对单元素/空数组序列化有问题，手动构造 JSON 数组
    $arr = @($bodyObj)
    $json = '[' + (($arr | ForEach-Object { $_ }) -join ',') + ']'
    $resp = Invoke-WebRequest -Uri $url -Method Delete -Body $json -ContentType 'application/json; charset=utf-8' -Headers $headers -UseBasicParsing
    return ($resp.Content | ConvertFrom-Json)
}

# ========== 功能 2：品种详情 ==========
# 1. 品种列表分页（免登录）
$breedList = Get-Url "$base/breed/list?page=1&size=12" @{}
Write-Host "[1] Breed List (public): code=$($breedList.code) total=$($breedList.data.total)"
if ($breedList.data.list.Count -gt 0) {
    $firstBreed = $breedList.data.list[0]
    Write-Host "    First: id=$($firstBreed.id) name=$($firstBreed.breedName) type=$($firstBreed.petType)"
    $breedId = $firstBreed.id
} else {
    Write-Host "    ERROR: No breeds found"
    exit 1
}

# 2. 按类型筛选
$dogList = Get-Url "$base/breed/list?petType=`$([System.Uri]::EscapeDataString('狗'))" @{}
Write-Host "[2] Filter by type=狗: total=$($dogList.data.total)"
foreach ($b in $dogList.data.list) {
    if ($b.petType -ne '狗') { Write-Host "    ERROR: type mismatch"; exit 1 }
}

# 3. 关键词搜索
$searchList = Get-Url "$base/breed/list?keyword=`$([System.Uri]::EscapeDataString('金'))" @{}
Write-Host "[3] Search keyword='金': total=$($searchList.data.total)"

# 4. 品种详情
$breedDetail = Get-Url "$base/breed/$breedId" @{}
Write-Host "[4] Breed Detail: code=$($breedDetail.code) name=$($breedDetail.data.breedName) desc_len=$($breedDetail.data.description.Length)"

# 5. 按品种名查询（供识别记录跳转）
$byName = Get-Url "$base/breed/by-name?name=`$([System.Uri]::EscapeDataString('金毛'))" @{}
Write-Host "[5] Breed by name '金毛': code=$($byName.code)"
if ($byName.data) {
    Write-Host "    Match: id=$($byName.data.id) name=$($byName.data.breedName)"
}

# 6. 品种不存在 - 40401
try {
    Get-Url "$base/breed/99999" @{}
    Write-Host "[6] Breed not found: UNEXPECTED success"
} catch {
    Write-Host "[6] Breed not found: correctly rejected"
}

# ========== 功能 1：识别历史记录批量删除 ==========
# 7. 用户登录
$loginResp = Post-Json "$base/auth/login" @{account='testuser'; password='123456'} @{}
Write-Host "[7] Login: code=$($loginResp.code)"
$token = $loginResp.data.token
$headers = @{Authorization = "Bearer $token"}

# 8. 我的识别记录分页
$myRecords = Get-Url "$base/recognition/my/page?page=1&size=10" $headers
Write-Host "[8] My Records: code=$($myRecords.code) total=$($myRecords.data.total)"

# 9. 先保存一条识别记录用于批量删除测试
$saveResp = Post-Json "$base/recognition/save" @{
    imageUrl='/uploads/test.png'
    petType='狗'
    breed='哈士奇'
    confidence=88.5
    success=1
} $headers
Write-Host "[9] Save record: code=$($saveResp.code) recordId=$($saveResp.data)"
$recordId = $saveResp.data

# 10. 批量删除
$delResp = Delete-Json "$base/recognition/my/batch" @($recordId) $headers
Write-Host "[10] Batch Delete: code=$($delResp.code) deletedCount=$($delResp.data)"

# 11. 验证已删除
$myRecords2 = Get-Url "$base/recognition/my/page?page=1&size=50" $headers
$found = $false
foreach ($r in $myRecords2.data.list) {
    if ($r.id -eq $recordId) { $found = $true; break }
}
if ($found) {
    Write-Host "[11] Verify Delete: ERROR (record still exists)"
} else {
    Write-Host "[11] Verify Delete: record correctly removed"
}

# 12. 批量删除 - 空数组
$emptyDel = Delete-Json "$base/recognition/my/batch" @() $headers
Write-Host "[12] Batch Delete empty: code=$($emptyDel.code) deletedCount=$($emptyDel.data)"

# ========== 功能 3：一键建档跳转 ==========
# 13. 一键建档
$archiveResp = Post-Json "$base/archive" @{
    petName='一键建档测试'
    breed='金毛'
    petType='狗'
    gender='公'
    age=2
    photoUrl='/uploads/test.png'
    source='ai'
} $headers
Write-Host "[13] One-Click Archive: code=$($archiveResp.code) archiveId=$($archiveResp.data)"
$archiveId = $archiveResp.data

# 14. 档案详情（用于跳转目标）
$archiveDetail = Get-Url "$base/archive/$archiveId" $headers
Write-Host "[14] Archive Detail: code=$($archiveDetail.code) name=$($archiveDetail.data.petName) source=$($archiveDetail.data.source)"

# 15. 清理测试档案
$delArchive = Invoke-WebRequest -Uri "$base/archive/$archiveId" -Method Delete -Headers $headers -UseBasicParsing
$delArchiveResp = $delArchive.Content | ConvertFrom-Json
Write-Host "[15] Delete Archive: code=$($delArchiveResp.code)"

Write-Host "`n========== Breed & Batch Delete & Archive Tests Done =========="
