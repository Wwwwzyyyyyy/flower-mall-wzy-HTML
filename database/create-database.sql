-- 在MySQL 8.0中由具备权限的运维账号执行；请替换密码。
CREATE DATABASE IF NOT EXISTS flower_mall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'flower'@'localhost' IDENTIFIED BY 'CHANGE_THIS_PASSWORD';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, INDEX, REFERENCES ON flower_mall.* TO 'flower'@'localhost';
-- 应用首次启动自动创建业务表，随后可撤销CREATE/INDEX/REFERENCES等DDL权限。
