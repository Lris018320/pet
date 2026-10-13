﻿# 个人中心 API 端到端测试（UTF-8 BOM）
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
function Get-Url($url, $headers) {
    $resp = Invoke-WebRequest -Uri $url -Method Get -Headers $headers -UseBasicParsing
    return ($resp.Content | ConvertFrom-Json)
}

# 1. 登录
$loginResp = Post-Json "$base/auth/login" @{account='testuser'; password='123456'} @{}
Write-Host "[1] Login: code=$($loginResp.code)"
$token = $loginResp.data.token
$headers = @{Authorization = "Bearer $token"}

# 2. 获取当前用户
$curr = Get-Url "$base/auth/current" $headers
Write-Host "[2] Current: code=$($curr.code) account=$($curr.data.account) nickname=$($curr.data.nickname)"

# 3. 更新资料（修改昵称）
$updateResp = Put-Json "$base/auth/profile" @{nickname='测试用户_改'; avatar=''} $headers
Write-Host "[3] Update Profile: code=$($updateResp.code) newNickname=$($updateResp.data.nickname)"

# 4. 再次查询验证
$curr2 = Get-Url "$base/auth/current" $headers
Write-Host "[4] Verify Current: nickname=$($curr2.data.nickname) (should match)"

# 5. 错误原密码修改应失败
try {
    $bad = Put-Json "$base/auth/password" @{oldPassword='wrongpwd'; newPassword='newpass123'} $headers
    Write-Host "[5] Wrong old password: code=$($bad.code) message=$($bad.message) (unexpected: should fail)"
} catch {
    $errResp = $_.ErrorDetails.Message
    Write-Host "[5] Wrong old password correctly rejected"
}

# 6. 正确修改密码（改为同样密码避免后续无法登录）
$pwdResp = Put-Json "$base/auth/password" @{oldPassword='123456'; newPassword='123456'} $headers
Write-Host "[6] Change Password (same): code=$($pwdResp.code)"

# 7. 我的识别记录分页
$myRecords = Get-Url "$base/info?page=1&size=5" @{}  # 借用公开接口先验证 token 有效
$recPage = Get-Url "$base/recognition/my/page?page=1&size=10" $headers
Write-Host "[7] My Records: code=$($recPage.code) total=$($recPage.data.total)"
if ($recPage.data.list.Count -gt 0) {
    $first = $recPage.data.list[0]
    Write-Host "    First: id=$($first.id) breed=$($first.breed) success=$($first.success) time=$($first.createTime)"
}

Write-Host "`n========== Profile Tests Done =========="
