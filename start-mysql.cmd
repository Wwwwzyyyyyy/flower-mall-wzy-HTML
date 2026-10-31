@echo off
chcp 65001 >nul
cd /d "%~dp0backend"
if "%JWT_SECRET%"=="" (
 echo 请先配置JWT_SECRET、ADMIN_PASSWORD、DB_USER、DB_PASSWORD环境变量，详见docs\部署说明.md。
 pause
 exit /b 1
)
set SPRING_PROFILES_ACTIVE=mysql
java -jar target\flower-mall-1.0.0.jar
pause
