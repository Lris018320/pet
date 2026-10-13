# AI 养护问答端到端测试（UTF-8 BOM）
$ErrorActionPreference = 'Stop'
$base = 'http://localhost:8080/api'

function Post-Json($url, $bodyObj, $headers) {
    $json = $bodyObj | ConvertTo-Json -Compress
    # PS 5.1 中文需显式按 UTF-8 字节发送
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($json)
    $resp = Invoke-WebRequest -Uri $url -Method Post -Body $bytes -ContentType 'application/json; charset=utf-8' -Headers $headers -UseBasicParsing
    $text = [System.Text.Encoding]::UTF8.GetString($resp.RawContentStream.ToArray())
    return ($text | ConvertFrom-Json)
}
function Get-Url($url, $headers) {
    $resp = Invoke-WebRequest -Uri $url -Method Get -Headers $headers -UseBasicParsing
    $text = [System.Text.Encoding]::UTF8.GetString($resp.RawContentStream.ToArray())
    return ($text | ConvertFrom-Json)
}
function Delete-Url($url, $headers) {
    $resp = Invoke-WebRequest -Uri $url -Method Delete -Headers $headers -UseBasicParsing
    $text = [System.Text.Encoding]::UTF8.GetString($resp.RawContentStream.ToArray())
    return ($text | ConvertFrom-Json)
}

# ========== 1. 未登录访问应被拦截（业务异常返回 code=40101） ==========
$noAuth = Post-Json "$base/chat/send" @{content='测试'} @{}
if ($noAuth.code -eq 40101) {
    Write-Host "[1] No-auth send: correctly rejected (code=40101)"
} else {
    Write-Host "[1] No-auth send: UNEXPECTED code=$($noAuth.code)"
    exit 1
}

# ========== 2. 用户登录 ==========
$loginResp = Post-Json "$base/auth/login" @{account='testuser'; password='123456'} @{}
Write-Host "[2] Login: code=$($loginResp.code)"
$token = $loginResp.data.token
$headers = @{Authorization = "Bearer $token"}

# ========== 3. 清空历史，保证干净起点 ==========
$clear0 = Delete-Url "$base/chat/history" $headers
Write-Host "[3] Pre-clean: code=$($clear0.code) deleted=$($clear0.data)"

# ========== 4. 饮食禁忌问答 ==========
$r1 = Post-Json "$base/chat/send" @{content='狗狗能吃巧克力吗？'} $headers
Write-Host "[4] Q: 狗狗能吃巧克力吗？"
Write-Host "    A: $($r1.data.content.Substring(0, [Math]::Min(60, $r1.data.content.Length)))..."
if ($r1.data.role -ne 'assistant' -or $r1.data.content -notmatch '巧克力') { Write-Host "    ERROR: unexpected reply"; exit 1 }

# ========== 5. 品种养护问答（命中 breed_knowledge） ==========
$r2 = Post-Json "$base/chat/send" @{content='金毛怎么养？'} $headers
Write-Host "[5] Q: 金毛怎么养？"
Write-Host "    A: $($r2.data.content.Substring(0, [Math]::Min(60, $r2.data.content.Length)))..."
if ($r2.data.content -notmatch '金毛') { Write-Host "    ERROR: breed not matched"; exit 1 }

# ========== 6. 洗澡问答 ==========
$r3 = Post-Json "$base/chat/send" @{content='猫咪多久洗一次澡？'} $headers
Write-Host "[6] Q: 猫咪多久洗一次澡？"
Write-Host "    A: $($r3.data.content.Substring(0, [Math]::Min(60, $r3.data.content.Length)))..."
if ($r3.data.content -notmatch '洗澡|洗一次') { Write-Host "    ERROR: bath topic not matched"; exit 1 }

# ========== 7. 疫苗问答 ==========
$r4 = Post-Json "$base/chat/send" @{content='幼犬要打什么疫苗？'} $headers
Write-Host "[7] Q: 幼犬要打什么疫苗？"
Write-Host "    A: $($r4.data.content.Substring(0, [Math]::Min(60, $r4.data.content.Length)))..."
if ($r4.data.content -notmatch '疫苗') { Write-Host "    ERROR: vaccine topic not matched"; exit 1 }

# ========== 8. 疾病问答 ==========
$r5 = Post-Json "$base/chat/send" @{content='宠物拉肚子呕吐怎么办？'} $headers
Write-Host "[8] Q: 宠物拉肚子呕吐怎么办？"
Write-Host "    A: $($r5.data.content.Substring(0, [Math]::Min(60, $r5.data.content.Length)))..."
if ($r5.data.content -notmatch '就医|医院') { Write-Host "    ERROR: disease topic not matched"; exit 1 }

# ========== 9. 打招呼 ==========
$r6 = Post-Json "$base/chat/send" @{content='你好'} $headers
Write-Host "[9] Q: 你好"
Write-Host "    A: $($r6.data.content.Substring(0, [Math]::Min(50, $r6.data.content.Length)))..."
if ($r6.data.content -notmatch '养护小助手') { Write-Host "    ERROR: greeting not matched"; exit 1 }

# ========== 10. 兜底回复 ==========
$r7 = Post-Json "$base/chat/send" @{content='火星天气怎么样'} $headers
Write-Host "[10] Q: 火星天气怎么样 (fallback)"
if ($r7.data.content -notmatch '学习中|试试') { Write-Host "    ERROR: fallback not matched"; exit 1 }
Write-Host "    A: $($r7.data.content.Substring(0, [Math]::Min(50, $r7.data.content.Length)))..."

# ========== 11. 空消息校验（返回 code=40001） ==========
$empty = Post-Json "$base/chat/send" @{content='   '} $headers
if ($empty.code -eq 40001) {
    Write-Host "[11] Empty message: correctly rejected (code=40001)"
} else {
    Write-Host "[11] Empty message: UNEXPECTED code=$($empty.code)"
    exit 1
}

# ========== 12. 历史记录 ==========
$hist = Get-Url "$base/chat/history" $headers
Write-Host "[12] History: code=$($hist.code) count=$($hist.data.Count)"
if ($hist.data.Count -lt 14) { Write-Host "    ERROR: history incomplete"; exit 1 }
if ($hist.data[0].role -ne 'user') { Write-Host "    ERROR: history order wrong"; exit 1 }

# ========== 13. 清空对话 ==========
$del = Delete-Url "$base/chat/history" $headers
Write-Host "[13] Clear history: code=$($del.code) deleted=$($del.data)"
$hist2 = Get-Url "$base/chat/history" $headers
if ($hist2.data.Count -ne 0) { Write-Host "    ERROR: history not cleared"; exit 1 }
Write-Host "[13] Verify cleared: count=0 OK"

Write-Host "`n========== AI Chat Tests All Passed =========="
