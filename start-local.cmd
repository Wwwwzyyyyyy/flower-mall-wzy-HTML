@echo off
chcp 65001 >nul
cd /d "%~dp0backend"
echo 花礼商城正在启动，约需10秒。
echo 用户端：http://127.0.0.1:8080/
echo 商家端：http://127.0.0.1:8080/#/admin
echo 关闭本窗口即可停止。默认使用本地持久化演示数据库。
java -jar target\flower-mall-1.0.0.jar
pause
