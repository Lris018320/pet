﻿# 阶段3 端到端测试：档案CRUD + 资讯浏览 + 后台资讯管理（UTF-8 BOM）
$ErrorActionPreference = 'Stop'
$base = 'http://localhost:8080/api'

function Post-Json($url, $bodyObj, $headers) {
    $json = $bodyObj | ConvertTo-Json -Compress
    $resp = Invoke-WebRequest -Uri $url -Method Post -Body $json -ContentType 'application/json; charset=utf-8' -Headers $headers -UseBasicParsing
    return ($resp.Content | ConvertFrom-Json)
}
function Put-Json($url, $bodyObj, $headers) {
    $json = $bodyObj | ConvertTo-Json -Compress
    $resp = Invoke-WebRequest -Uri $url -Method Put -Body $json -ContentType 'application/json; charset=utf-8' -Headers $headers -UseBasicParsing
    return ($resp.Content | ConvertFrom-Json)
}
function Delete-Url($url, $headers) {
    $resp = Invoke-WebRequest -Uri $url -Method Delete -Headers $headers -UseBasicParsing
    return ($resp.Content | ConvertFrom-Json)
}
function Get-Url($url, $headers) {
    $resp = Invoke-WebRequest -Uri $url -Method Get -Headers $headers -UseBasicParsing
    return ($resp.Content | ConvertFrom-Json)
}

# ====== 用户侧：档案 CRUD ======
$loginResp = Post-Json "$base/auth/login" @{account='testuser'; password='123456'} @{}
Write-Host "[1] User Login: code=$($loginResp.code)"
$token = $loginResp.data.token
$headers = @{Authorization = "Bearer $token"}

# 2. 创建档案
$createResp = Post-Json "$base/archive" @{
    petName = '小白'
    breed = '布偶猫'
    petType = '猫'
    age = '3岁'
    gender = '母'
    source = 'manual'
} $headers
Write-Host "[2] Create Archive: code=$($createResp.code) id=$($createResp.data)"
$archiveId = $createResp.data

# 3. 查询列表
$listResp = Get-Url "$base/archive/list" $headers
Write-Host "[3] List Archives: code=$($listResp.code) count=$($listResp.data.Count)"

# 4. 查询详情
$detailResp = Get-Url "$base/archive/$archiveId" $headers
Write-Host "[4] Get Detail: code=$($detailResp.code) name=$($detailResp.data.petName)"

# 5. 修改档案
$updateResp = Put-Json "$base/archive/$archiveId" @{
    petName = '小白改名'
    breed = '布偶猫'
    petType = '猫'
    age = '4岁'
    gender = '母'
} $headers
Write-Host "[5] Update Archive: code=$($updateResp.code)"

# 6. 删除档案
$delResp = Delete-Url "$base/archive/$archiveId" $headers
Write-Host "[6] Delete Archive: code=$($delResp.code)"

# ====== 前台资讯浏览（免登录）======
Write-Host "`n[7] Info List (public):"
$infoList = Get-Url "$base/info/list?page=1&size=5" @{}
Write-Host "    code=$($infoList.code) total=$($infoList.data.total)"
if ($infoList.data.list.Count -gt 0) {
    $firstInfo = $infoList.data.list[0]
    Write-Host "    First: id=$($firstInfo.id) title=$($firstInfo.title) category=$($firstInfo.category)"
    # 8. 资讯详情
    $infoDetail = Get-Url "$base/info/$($firstInfo.id)" @{}
    Write-Host "[8] Info Detail: code=$($infoDetail.code) hasContent=$(-not [string]::IsNullOrEmpty($infoDetail.data.content))"
}

# ====== 后台：资讯管理 ======
$adminLogin = Post-Json "$base/admin/login" @{account='admin'; password='123456'} @{}
Write-Host "`n[9] Admin Login: code=$($adminLogin.code)"
$adminToken = $adminLogin.data.token
$adminHeaders = @{Authorization = "Bearer $adminToken"}

# 10. 后台资讯分页
$adminPage = Get-Url "$base/admin/info/page?page=1&size=5" $adminHeaders
Write-Host "[10] Admin Info Page: code=$($adminPage.code) total=$($adminPage.data.total)"

# 11. 新增资讯
$newInfo = Post-Json "$base/admin/info" @{
    title = '测试资讯-请删除'
    content = '<p>这是测试内容</p>'
    category = '喂养'
    status = 1
} $adminHeaders
Write-Host "[11] Admin Create Info: code=$($newInfo.code) id=$($newInfo.data)"
$newInfoId = $newInfo.data

# 12. 修改资讯
$editInfo = Put-Json "$base/admin/info/$newInfoId" @{
    title = '测试资讯-修改后'
    content = '<p>修改后的内容</p>'
    category = '洗护'
    status = 1
} $adminHeaders
Write-Host "[12] Admin Update Info: code=$($editInfo.code)"

# 13. 下架
$statusResp = Invoke-WebRequest -Uri "$base/admin/info/$newInfoId/status?status=0" -Method Put -Headers $adminHeaders -UseBasicParsing
$statusJson = $statusResp.Content | ConvertFrom-Json
Write-Host "[13] Admin Update Status(0): code=$($statusJson.code)"

# 14. 验证前台无法访问已下架资讯
$publicCheck = Get-Url "$base/info/$newInfoId" @{}
Write-Host "[14] Public Access (status=0): code=$($publicCheck.code) message=$($publicCheck.message)"

# 15. 删除资讯
$delInfo = Delete-Url "$base/admin/info/$newInfoId" $adminHeaders
Write-Host "[15] Admin Delete Info: code=$($delInfo.code)"

Write-Host "`n========== Phase 3 Tests Done =========="
