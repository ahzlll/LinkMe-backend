-- 迁移脚本：将 post_image 表的 image_url 字段从 VARCHAR(255) 改为 LONGTEXT
-- 用于支持存储 Base64 编码的图片字符串
-- 执行时间：建议在维护窗口期间执行

USE linkme;

-- 修改 image_url 字段类型为 LONGTEXT
ALTER TABLE post_image 
MODIFY COLUMN image_url LONGTEXT NOT NULL COMMENT '图片Base64编码字符串';

-- 验证修改
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    CHARACTER_MAXIMUM_LENGTH,
    COLUMN_COMMENT
FROM 
    INFORMATION_SCHEMA.COLUMNS
WHERE 
    TABLE_SCHEMA = 'linkme' 
    AND TABLE_NAME = 'post_image' 
    AND COLUMN_NAME = 'image_url';

-- 修改 avatar_url 字段类型为 LONGTEXT
ALTER TABLE user
MODIFY COLUMN avatar_url LONGTEXT  COMMENT '图片Base64编码字符串';
