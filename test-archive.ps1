﻿# 一键建档 API 测试（UTF-8 BOM）
$ErrorActionPreference = 'Stop'
$base = 'http://localhost:8080/api'

function Post-Json($url, $bodyObj, $headers) {
    $json = $bodyObj | ConvertTo-Json -Compress
    $resp = Invoke-WebRequest -Uri $url -Method Post -Body $json -ContentType 'application/json; charset=utf-8' -Headers $headers -UseBasicParsing
    return ($resp.Content | ConvertFrom-Json)
}

# 1. 登录
$loginResp = Post-Json "$base/auth/login" @{account='testuser'; password='123456'} @{}
Write-Host "[1] Login: code=$($loginResp.code)"
if ($loginResp.code -ne 200) { throw "Login failed" }
$token = $loginResp.data.token
$headers = @{Authorization = "Bearer $token"}

# 2. 一键建档
$archiveResp = Post-Json "$base/pet/archive" @{
    petName = '测试宠物'
    breed = '金毛'
    petType = '狗'
    age = '2岁'
    gender = '公'
    photoUrl = '/uploads/2026-09-06/test.png'
    source = 'ai'
} $headers
Write-Host "[2] Create Archive: code=$($archiveResp.code) archiveId=$($archiveResp.data)"

# 3. 查询我的档案列表
$listResp = Invoke-WebRequest -Uri "$base/pet/archive/list" -Method Get -Headers $headers -UseBasicParsing
$listJson = $listResp.Content | ConvertFrom-Json
Write-Host "[3] List My Archives: code=$($listJson.code) count=$($listJson.data.Count)"
if ($listJson.data.Count -gt 0) {
    $first = $listJson.data[0]
    Write-Host "    First: id=$($first.id) name=$($first.petName) breed=$($first.breed) source=$($first.source)"
}
