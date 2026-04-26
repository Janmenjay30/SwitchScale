param(
    [int]$NotificationPort = 8008,
    [switch]$IncludeFrontends,
    [switch]$DryRun
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $MyInvocation.MyCommand.Path

function Import-DotEnv {
    param(
        [string]$DotEnvPath
    )

    if (-not (Test-Path -LiteralPath $DotEnvPath)) {
        Write-Host "No .env file found at $DotEnvPath. Using defaults from service configs." -ForegroundColor DarkYellow
        return
    }

    $loaded = 0
    foreach ($rawLine in Get-Content -LiteralPath $DotEnvPath) {
        $line = $rawLine.Trim()
        if ([string]::IsNullOrWhiteSpace($line) -or $line.StartsWith("#")) {
            continue
        }

        if ($line.StartsWith("export ")) {
            $line = $line.Substring(7).Trim()
        }

        $parts = $line -split "=", 2
        if ($parts.Count -ne 2) {
            continue
        }

        $key = $parts[0].Trim()
        $value = $parts[1].Trim().Trim('"').Trim("'")

        if ([string]::IsNullOrWhiteSpace($key)) {
            continue
        }

        Set-Item -Path "Env:$key" -Value $value
        $loaded++
    }

    Write-Host "Loaded $loaded variable(s) from .env" -ForegroundColor Green
}

Import-DotEnv -DotEnvPath (Join-Path $root ".env")

$services = @(
    @{
        Name = "eureka-server"
        Path = "eureka-server"
        Command = "mvn spring-boot:run"
    },
    @{
        Name = "userservice"
        Path = "userservice"
        Command = "mvn spring-boot:run"
    },
    @{
        Name = "catalog"
        Path = "catalog"
        Command = "mvn spring-boot:run"
    },
    @{
        Name = "inventory"
        Path = "inventory"
        Command = "mvn spring-boot:run"
    },
    @{
        Name = "cart"
        Path = "cart"
        Command = "mvn spring-boot:run"
    },
    @{
        Name = "payment"
        Path = "payment"
        Command = "mvn spring-boot:run"
    },
    @{
        Name = "delivery"
        Path = "delivery"
        Command = "mvn spring-boot:run"
    },
    @{
        Name = "notification"
        Path = "notification"
        Command = if ($NotificationPort -eq 8008) {
            "mvn spring-boot:run"
        } else {
            "mvn spring-boot:run `"-Dspring-boot.run.arguments=--server.port=$NotificationPort`""
        }
    },
    @{
        Name = "order"
        Path = "order"
        Command = "mvn spring-boot:run"
    }
)

if ($IncludeFrontends) {
    $services += @(
        @{
            Name = "switchscale-frontend"
            Path = "switchscale-frontend"
            Command = "npm run dev"
        },
        @{
            Name = "darkstore_frontend"
            Path = "darkstore_frontend"
            Command = "npm run dev"
        }
    )
}

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    throw "Maven (mvn) was not found in PATH."
}

if ($IncludeFrontends -and -not (Get-Command npm -ErrorAction SilentlyContinue)) {
    throw "npm was not found in PATH."
}

function Start-ServiceWindow {
    param(
        [string]$Name,
        [string]$ServicePath,
        [string]$ServiceCommand
    )

    $absolutePath = Join-Path $root $ServicePath
    if (-not (Test-Path -LiteralPath $absolutePath)) {
        Write-Warning "Skipping $Name because path was not found: $absolutePath"
        return
    }

    $bootstrap = @"
`$Host.UI.RawUI.WindowTitle = 'Blinkit - $Name'
Set-Location -LiteralPath '$absolutePath'
Write-Host 'Starting $Name in $absolutePath' -ForegroundColor Cyan
$ServiceCommand
"@

    if ($DryRun) {
        Write-Host "[DRY RUN] $Name => $absolutePath => $ServiceCommand"
        return
    }

    Start-Process -FilePath "powershell.exe" -ArgumentList @(
        "-NoExit",
        "-ExecutionPolicy",
        "Bypass",
        "-Command",
        $bootstrap
    ) | Out-Null
}

Write-Host "Launching Blinkit services from: $root" -ForegroundColor Green
foreach ($service in $services) {
    Start-ServiceWindow -Name $service.Name -ServicePath $service.Path -ServiceCommand $service.Command
}

if ($DryRun) {
    Write-Host "Dry run complete. No terminals were launched." -ForegroundColor Yellow
} else {
    Write-Host "All service terminals launched." -ForegroundColor Green
}
