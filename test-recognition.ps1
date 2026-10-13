﻿# 端到端测试：JSON 用 Invoke-WebRequest，multipart 用 curl.exe
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
if ($loginResp.code -ne 200) { throw "Login failed: $($loginResp | ConvertTo-Json)" }
$token = $loginResp.data.token
Write-Host "    token=$($token.Substring(0,40))..."
$headers = @{Authorization = "Bearer $token"}

# 2. 生成测试图片（1x1 PNG）
$pngB64 = 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=='
$pngBytes = [Convert]::FromBase64String($pngB64)
Write-Host "[2] Base64 decoded: $($pngBytes.Length) bytes"
$testFile = Join-Path $env:TEMP "test-pet-$([System.Guid]::NewGuid().ToString('N').Substring(0,8)).png"
if (Test-Path $testFile) { Remove-Item $testFile -Force }
[System.IO.File]::WriteAllBytes($testFile, $pngBytes)
$len = (Get-Item $testFile).Length
Write-Host "[2] Test PNG created: $testFile ($len bytes)"
if ($len -eq 0) { throw "PNG file is empty" }

# 3. 上传图片（curl.exe 处理 multipart）
$uploadRaw = curl.exe -s -X POST "$base/recognition/upload" -H "Authorization: Bearer $token" -F "file=@$testFile;type=image/png" --max-time 15
Write-Host "[3] Upload raw: $uploadRaw"
$uploadJson = $uploadRaw | ConvertFrom-Json
if ($uploadJson.code -ne 200) { throw "Upload failed: $uploadRaw" }
$imageUrl = $uploadJson.data.imageUrl
Write-Host "    imageUrl=$imageUrl"

# 4. 调用 AI 识别
$recResp = Post-Json "$base/recognition/recognize" @{imageUrl=$imageUrl} $headers
Write-Host "[4] Recognize: code=$($recResp.code) success=$($recResp.data.success) breed=$($recResp.data.breed) confidence=$($recResp.data.confidence)"
if ($recResp.code -ne 200 -or -not $recResp.data.success) {
    Write-Host "    message: $($recResp.data.message)"
    throw "Recognize failed"
}

# 5. 保存识别记录
$saveResp = Post-Json "$base/recognition/save" @{
    imageUrl = $imageUrl
    petType = $recResp.data.petType
    breed = $recResp.data.breed
    confidence = $recResp.data.confidence
    success = 1
} $headers
Write-Host "[5] Save: code=$($saveResp.code) recordId=$($saveResp.data)"

# 6. 验证 MySQL 记录
Write-Host "`n[6] MySQL verification:"
$mysql = 'D:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe'
$query = "USE pet_platform; SELECT id, user_id, breed, confidence, success FROM recognition_record ORDER BY id DESC LIMIT 3;"
& $mysql -uroot -p123456 -e $query 2>$null
