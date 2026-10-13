﻿# 后台管理 API 端到端测试（UTF-8 BOM）
$ErrorActionPreference = 'Stop'
$base = 'http://localhost:8080/api'

function Post-Json($url, $bodyObj, $headers) {
    $json = $bodyObj | ConvertTo-Json -Compress
    $resp = Invoke-WebRequest -Uri $url -Method Post -Body $json -ContentType 'application/json; charset=utf-8' -Headers $headers -UseBasicParsing
    return ($resp.Content | ConvertFrom-Json)
}
function Put-Url($url, $headers) {
    $resp = Invoke-WebRequest -Uri $url -Method Put -Headers $headers -UseBasicParsing
    return ($resp.Content | ConvertFrom-Json)
}
function Get-Url($url, $headers) {
    $resp = Invoke-WebRequest -Uri $url -Method Get -Headers $headers -UseBasicParsing
    return ($resp.Content | ConvertFrom-Json)
}

# 管理员登录
$loginResp = Post-Json "$base/admin/login" @{account='admin'; password='123456'} @{}
Write-Host "[1] Admin Login: code=$($loginResp.code)"
$token = $loginResp.data.token
$adminHeaders = @{Authorization = "Bearer $token"}

# 数据看板
$dash = Get-Url "$base/admin/dashboard" $adminHeaders
Write-Host "[2] Dashboard: code=$($dash.code) userCount=$($dash.data.userCount) recCount=$($dash.data.recognitionCount) infoCount=$($dash.data.infoCount) todayRec=$($dash.data.todayRecognitionCount)"

# 用户分页
$userPage = Get-Url "$base/admin/user/page?page=1&size=10" $adminHeaders
Write-Host "[3] User Page: code=$($userPage.code) total=$($userPage.data.total)"
if ($userPage.data.list.Count -gt 0) {
    $u = $userPage.data.list[0]
    Write-Host "    First user: id=$($u.id) account=$($u.account) status=$($u.status)"
    $userId = $u.id

    # 禁用用户
    $disable = Put-Url "$base/admin/user/$userId/status?status=0" $adminHeaders
    Write-Host "[4] Disable User: code=$($disable.code) result=$($disable.data)"
    # 启用用户
    $enable = Put-Url "$base/admin/user/$userId/status?status=1" $adminHeaders
    Write-Host "[5] Enable User: code=$($enable.code) result=$($enable.data)"
}

# 识别记录分页
$recPage = Get-Url "$base/admin/recognition/page?page=1&size=10" $adminHeaders
Write-Host "[6] Recognition Page: code=$($recPage.code) total=$($recPage.data.total)"

# 资讯管理（已在阶段3验证过，这里再快速回归）
$infoPage = Get-Url "$base/admin/info/page?page=1&size=5" $adminHeaders
Write-Host "[7] Admin Info Page: code=$($infoPage.code) total=$($infoPage.data.total)"

# 普通用户访问后台接口应 403/无权限
$userLogin = Post-Json "$base/auth/login" @{account='testuser'; password='123456'} @{}
$userToken = $userLogin.data.token
$userHeaders = @{Authorization = "Bearer $userToken"}
try {
    $forbidden = Get-Url "$base/admin/dashboard" $userHeaders
    Write-Host "[8] Normal user access admin: code=$($forbidden.code) (unexpected: should fail)"
} catch {
    $err = $_.Exception.Message
    if ($err -match '403|无权限') {
        Write-Host "[8] Normal user blocked by AdminInterceptor (correct)"
    } else {
        Write-Host "[8] Unexpected error: $err"
    }
}

Write-Host "`n========== Admin UI Backend Tests Done =========="
