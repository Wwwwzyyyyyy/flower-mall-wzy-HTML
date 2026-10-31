@echo off
chcp 65001 >nul
cd /d "%~dp0frontend"
call npm.cmd ci --no-audit --no-fund
if errorlevel 1 exit /b 1
call npm.cmd run build
if errorlevel 1 exit /b 1
cd ..\backend
call mvn.cmd -s settings-cn.xml clean package
if errorlevel 1 exit /b 1
echo 前后端构建完成，运行项目根目录start-local.cmd即可体验。
pause
