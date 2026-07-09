-- ============================================
-- 修复爱好数据中的乱码问题
-- 问题描述："露营"在数据库中显示为乱码"¶Ӫ"
-- 原因：数据库连接字符集配置不正确导致UTF-8编码被错误存储
-- ============================================

-- 1. 查看当前爱好表中是否存在乱码数据
SELECT hobby_id, category_id, name, display_order FROM hobby WHERE name = '¶Ӫ';

-- 2. 修复乱码数据：将"¶Ӫ"改为"露营"
UPDATE hobby SET name = '露营' WHERE name = '¶Ӫ';

-- 3. 验证修复结果
SELECT hobby_id, category_id, name, display_order FROM hobby WHERE category_id = 3 ORDER BY display_order;

-- 4. 同时检查爱好分类表是否有乱码
SELECT * FROM hobby_category WHERE name LIKE '%乱码%' OR name LIKE '%�%';

-- 5. 如果有其他乱码，也需要修复
-- UPDATE hobby SET name = '正确名称' WHERE name = '乱码名称';
