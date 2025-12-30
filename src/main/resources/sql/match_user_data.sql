-- ============================================
-- 匹配用户数据插入
-- 为测试用户插入完整的匹配问卷数据
-- ============================================

USE linkme;

-- ============================================
-- 1. 插入用户爱好关联（UserHobby）
-- ============================================
-- 根据用户的bio和标签，为每个用户选择3-6个相关爱好

-- 用户2：小明（程序员，喜欢旅游和摄影）
INSERT INTO user_hobby (user_id, hobby_id) VALUES
(2, 2),  -- 摄影
(2, 12), -- 编程
(2, 50), -- 旅行
(2, 55), -- 展览打卡
(2, 11), -- 阅读
(2, 21); -- 跑步

-- 用户3：小红（设计师，热爱音乐和美食）
INSERT INTO user_hobby (user_id, hobby_id) VALUES
(3, 9),  -- 平面设计
(3, 39), -- 听音乐
(3, 43), -- 烹饪/烘焙
(3, 54), -- 探店打卡
(3, 53), -- 演唱会
(3, 55); -- 展览打卡

-- 用户4：小丽（健身达人和读书爱好者）
INSERT INTO user_hobby (user_id, hobby_id) VALUES
(4, 11), -- 阅读
(4, 22), -- 健身
(4, 21), -- 跑步
(4, 26), -- 瑜伽
(4, 44), -- 咖啡/茶艺/调酒
(4, 50); -- 旅行

-- 用户5：大卫（创业者和投资人）
INSERT INTO user_hobby (user_id, hobby_id) VALUES
(5, 20), -- 创业项目
(5, 18), -- 投资理财
(5, 19), -- 公开演讲
(5, 11), -- 阅读
(5, 16), -- 哲学思考
(5, 50); -- 旅行

-- 用户6：莉莉（宠物医生和动物保护者）
INSERT INTO user_hobby (user_id, hobby_id) VALUES
(6, 11), -- 阅读
(6, 58), -- 撸猫撸狗
(6, 57), -- 公益志愿
(6, 43), -- 烹饪/烘焙
(6, 50), -- 旅行
(6, 44); -- 咖啡/茶艺/调酒

-- 用户7：Alex（产品经理）
INSERT INTO user_hobby (user_id, hobby_id) VALUES
(7, 12), -- 编程
(7, 11), -- 阅读
(7, 19), -- 公开演讲
(7, 16), -- 哲学思考
(7, 50), -- 旅行
(7, 55); -- 展览打卡

-- 用户8：Sarah（运营专员）
INSERT INTO user_hobby (user_id, hobby_id) VALUES
(8, 39), -- 听音乐
(8, 54), -- 探店打卡
(8, 50), -- 旅行
(8, 53), -- 演唱会
(8, 11), -- 阅读
(8, 43); -- 烹饪/烘焙

-- 用户9：Mike（工程师）
INSERT INTO user_hobby (user_id, hobby_id) VALUES
(9, 12), -- 编程
(9, 42), -- 电子游戏
(9, 11), -- 阅读
(9, 21), -- 跑步
(9, 50), -- 旅行
(9, 33); -- 桌游

-- 用户10：Emily（教师）
INSERT INTO user_hobby (user_id, hobby_id) VALUES
(10, 13), -- 教学
(10, 11), -- 阅读
(10, 4),  -- 写作
(10, 50), -- 旅行
(10, 44), -- 咖啡/茶艺/调酒
(10, 26); -- 瑜伽

-- 用户11：Tom（市场专员）
INSERT INTO user_hobby (user_id, hobby_id) VALUES
(11, 50), -- 旅行
(11, 54), -- 探店打卡
(11, 19), -- 公开演讲
(11, 39), -- 听音乐
(11, 21), -- 跑步
(11, 55); -- 展览打卡

-- 用户12：Lisa（医生）
INSERT INTO user_hobby (user_id, hobby_id) VALUES
(12, 22), -- 健身
(12, 21), -- 跑步
(12, 26), -- 瑜伽
(12, 11), -- 阅读
(12, 50), -- 旅行
(12, 44); -- 咖啡/茶艺/调酒

-- 用户13：James（律师）
INSERT INTO user_hobby (user_id, hobby_id) VALUES
(13, 11), -- 阅读
(13, 16), -- 哲学思考
(13, 17), -- 历史研究
(13, 19), -- 公开演讲
(13, 50), -- 旅行
(13, 33); -- 桌游

-- 用户14：Anna（电影迷）
INSERT INTO user_hobby (user_id, hobby_id) VALUES
(14, 38), -- 看电影
(14, 39), -- 听音乐
(14, 11), -- 阅读
(14, 55), -- 展览打卡
(14, 50), -- 旅行
(14, 54); -- 探店打卡

-- 用户15：Jack（游戏玩家）
INSERT INTO user_hobby (user_id, hobby_id) VALUES
(15, 42), -- 电子游戏
(15, 33), -- 桌游
(15, 22), -- 健身
(15, 21), -- 跑步
(15, 50), -- 旅行
(15, 39); -- 听音乐

-- ============================================
-- 2. 插入用户性格特质（UserPersonality）
-- ============================================
-- 每个用户选择自身特质（self）和理想对象特质（ideal）

-- 用户2：小明 - 内向型，理性型，计划型，直接坦率型
-- 希望对方：同频即可，踏实靠谱，乐观积极
INSERT INTO user_personality (user_id, option_id) VALUES
(2, 2),  -- 自身：内向型（独处充电）
(2, 4),  -- 自身：理性型（逻辑优先）
(2, 7),  -- 自身：计划型（凡事按规划）
(2, 10), -- 自身：直接坦率型
(2, 17), -- 理想：同频即可
(2, 21), -- 理想：踏实靠谱
(2, 22); -- 理想：乐观积极

-- 用户3：小红 - 外向型，感性型，弹性型，幽默风趣型
-- 希望对方：热情健谈，灵活变通，乐观积极
INSERT INTO user_personality (user_id, option_id) VALUES
(3, 1),  -- 自身：外向型（社交充电）
(3, 5),  -- 自身：感性型（感受优先）
(3, 9),  -- 自身：弹性型
(3, 12), -- 自身：幽默风趣型
(3, 15), -- 理想：热情健谈
(3, 20), -- 理想：灵活变通
(3, 22); -- 理想：乐观积极

-- 用户4：小丽 - 中间型，平衡型，计划型，委婉体贴型
-- 希望对方：沉稳内敛，严谨细致，情绪稳定
INSERT INTO user_personality (user_id, option_id) VALUES
(4, 3),  -- 自身：中间型（看情况）
(4, 6),  -- 自身：平衡型
(4, 7),  -- 自身：计划型（凡事按规划）
(4, 11), -- 自身：委婉体贴型
(4, 16), -- 理想：沉稳内敛
(4, 18), -- 理想：严谨细致
(4, 25); -- 理想：情绪稳定

-- 用户5：大卫 - 外向型，理性型，计划型，直接坦率型
-- 希望对方：同频即可，高效行动，冷静理智
INSERT INTO user_personality (user_id, option_id) VALUES
(5, 1),  -- 自身：外向型（社交充电）
(5, 4),  -- 自身：理性型（逻辑优先）
(5, 7),  -- 自身：计划型（凡事按规划）
(5, 10), -- 自身：直接坦率型
(5, 17), -- 理想：同频即可
(5, 19), -- 理想：高效行动
(5, 23); -- 理想：冷静理智

-- 用户6：莉莉 - 外向型，感性型，弹性型，倾听为主型
-- 希望对方：热情健谈，灵活变通，敏感共情
INSERT INTO user_personality (user_id, option_id) VALUES
(6, 1),  -- 自身：外向型（社交充电）
(6, 5),  -- 自身：感性型（感受优先）
(6, 9),  -- 自身：弹性型
(6, 13), -- 自身：倾听为主型
(6, 15), -- 理想：热情健谈
(6, 20), -- 理想：灵活变通
(6, 24); -- 理想：敏感共情

-- 用户7：Alex - 中间型，理性型，计划型，直接坦率型
-- 希望对方：同频即可，严谨细致，冷静理智
INSERT INTO user_personality (user_id, option_id) VALUES
(7, 3),  -- 自身：中间型（看情况）
(7, 4),  -- 自身：理性型（逻辑优先）
(7, 7),  -- 自身：计划型（凡事按规划）
(7, 10), -- 自身：直接坦率型
(7, 17), -- 理想：同频即可
(7, 18), -- 理想：严谨细致
(7, 23); -- 理想：冷静理智

-- 用户8：Sarah - 外向型，感性型，弹性型，幽默风趣型
-- 希望对方：热情健谈，灵活变通，乐观积极
INSERT INTO user_personality (user_id, option_id) VALUES
(8, 1),  -- 自身：外向型（社交充电）
(8, 5),  -- 自身：感性型（感受优先）
(8, 9),  -- 自身：弹性型
(8, 12), -- 自身：幽默风趣型
(8, 15), -- 理想：热情健谈
(8, 20), -- 理想：灵活变通
(8, 22); -- 理想：乐观积极

-- 用户9：Mike - 内向型，理性型，计划型，偶尔沉默型
-- 希望对方：沉稳内敛，严谨细致，冷静理智
INSERT INTO user_personality (user_id, option_id) VALUES
(9, 2),  -- 自身：内向型（独处充电）
(9, 4),  -- 自身：理性型（逻辑优先）
(9, 7),  -- 自身：计划型（凡事按规划）
(9, 14), -- 自身：偶尔沉默型
(9, 16), -- 理想：沉稳内敛
(9, 18), -- 理想：严谨细致
(9, 23); -- 理想：冷静理智

-- 用户10：Emily - 外向型，感性型，计划型，委婉体贴型
-- 希望对方：热情健谈，踏实靠谱，敏感共情
INSERT INTO user_personality (user_id, option_id) VALUES
(10, 1),  -- 自身：外向型（社交充电）
(10, 5),  -- 自身：感性型（感受优先）
(10, 7),  -- 自身：计划型（凡事按规划）
(10, 11), -- 自身：委婉体贴型
(10, 15), -- 理想：热情健谈
(10, 21), -- 理想：踏实靠谱
(10, 24); -- 理想：敏感共情

-- 用户11：Tom - 外向型，平衡型，随性型，幽默风趣型
-- 希望对方：热情健谈，灵活变通，乐观积极
INSERT INTO user_personality (user_id, option_id) VALUES
(11, 1),  -- 自身：外向型（社交充电）
(11, 6),  -- 自身：平衡型
(11, 8),  -- 自身：随性型（走一步看一步）
(11, 12), -- 自身：幽默风趣型
(11, 15), -- 理想：热情健谈
(11, 20), -- 理想：灵活变通
(11, 22); -- 理想：乐观积极

-- 用户12：Lisa - 中间型，理性型，计划型，直接坦率型
-- 希望对方：同频即可，严谨细致，情绪稳定
INSERT INTO user_personality (user_id, option_id) VALUES
(12, 3),  -- 自身：中间型（看情况）
(12, 4),  -- 自身：理性型（逻辑优先）
(12, 7),  -- 自身：计划型（凡事按规划）
(12, 10), -- 自身：直接坦率型
(12, 17), -- 理想：同频即可
(12, 18), -- 理想：严谨细致
(12, 25); -- 理想：情绪稳定

-- 用户13：James - 内向型，理性型，计划型，直接坦率型
-- 希望对方：沉稳内敛，严谨细致，冷静理智
INSERT INTO user_personality (user_id, option_id) VALUES
(13, 2),  -- 自身：内向型（独处充电）
(13, 4),  -- 自身：理性型（逻辑优先）
(13, 7),  -- 自身：计划型（凡事按规划）
(13, 10), -- 自身：直接坦率型
(13, 16), -- 理想：沉稳内敛
(13, 18), -- 理想：严谨细致
(13, 23); -- 理想：冷静理智

-- 用户14：Anna - 中间型，感性型，弹性型，倾听为主型
-- 希望对方：同频即可，灵活变通，敏感共情
INSERT INTO user_personality (user_id, option_id) VALUES
(14, 3),  -- 自身：中间型（看情况）
(14, 5),  -- 自身：感性型（感受优先）
(14, 9),  -- 自身：弹性型
(14, 13), -- 自身：倾听为主型
(14, 17), -- 理想：同频即可
(14, 20), -- 理想：灵活变通
(14, 24); -- 理想：敏感共情

-- 用户15：Jack - 外向型，平衡型，随性型，幽默风趣型
-- 希望对方：热情健谈，灵活变通，乐观积极
INSERT INTO user_personality (user_id, option_id) VALUES
(15, 1),  -- 自身：外向型（社交充电）
(15, 6),  -- 自身：平衡型
(15, 8),  -- 自身：随性型（走一步看一步）
(15, 12), -- 自身：幽默风趣型
(15, 15), -- 理想：热情健谈
(15, 20), -- 理想：灵活变通
(15, 22); -- 理想：乐观积极

-- ============================================
-- 3. 插入用户关系品质（UserRelationshipQuality）
-- ============================================
-- 每个用户选择2-4个看重的品质

-- 用户2：小明
INSERT INTO user_relationship_quality (user_id, quality_id) VALUES
(2, 1), -- 真诚坦率
(2, 3), -- 彼此信任
(2, 6); -- 三观一致

-- 用户3：小红
INSERT INTO user_relationship_quality (user_id, quality_id) VALUES
(3, 1), -- 真诚坦率
(3, 2), -- 相互理解
(3, 5); -- 有趣合拍

-- 用户4：小丽
INSERT INTO user_relationship_quality (user_id, quality_id) VALUES
(4, 2), -- 相互理解
(4, 3), -- 彼此信任
(4, 4), -- 包容尊重
(4, 6); -- 三观一致

-- 用户5：大卫
INSERT INTO user_relationship_quality (user_id, quality_id) VALUES
(5, 1), -- 真诚坦率
(5, 3), -- 彼此信任
(5, 6); -- 三观一致

-- 用户6：莉莉
INSERT INTO user_relationship_quality (user_id, quality_id) VALUES
(6, 2), -- 相互理解
(6, 4), -- 包容尊重
(6, 5); -- 有趣合拍

-- 用户7：Alex
INSERT INTO user_relationship_quality (user_id, quality_id) VALUES
(7, 1), -- 真诚坦率
(7, 3), -- 彼此信任
(7, 6); -- 三观一致

-- 用户8：Sarah
INSERT INTO user_relationship_quality (user_id, quality_id) VALUES
(8, 1), -- 真诚坦率
(8, 2), -- 相互理解
(8, 5); -- 有趣合拍

-- 用户9：Mike
INSERT INTO user_relationship_quality (user_id, quality_id) VALUES
(9, 1), -- 真诚坦率
(9, 3), -- 彼此信任
(9, 6); -- 三观一致

-- 用户10：Emily
INSERT INTO user_relationship_quality (user_id, quality_id) VALUES
(10, 2), -- 相互理解
(10, 4), -- 包容尊重
(10, 5); -- 有趣合拍

-- 用户11：Tom
INSERT INTO user_relationship_quality (user_id, quality_id) VALUES
(11, 1), -- 真诚坦率
(11, 5), -- 有趣合拍
(11, 2); -- 相互理解

-- 用户12：Lisa
INSERT INTO user_relationship_quality (user_id, quality_id) VALUES
(12, 2), -- 相互理解
(12, 3), -- 彼此信任
(12, 4); -- 包容尊重

-- 用户13：James
INSERT INTO user_relationship_quality (user_id, quality_id) VALUES
(13, 1), -- 真诚坦率
(13, 3), -- 彼此信任
(13, 6); -- 三观一致

-- 用户14：Anna
INSERT INTO user_relationship_quality (user_id, quality_id) VALUES
(14, 2), -- 相互理解
(14, 5), -- 有趣合拍
(14, 4); -- 包容尊重

-- 用户15：Jack
INSERT INTO user_relationship_quality (user_id, quality_id) VALUES
(15, 1), -- 真诚坦率
(15, 5), -- 有趣合拍
(15, 2); -- 相互理解

-- ============================================
-- 4. 插入用户匹配偏好（UserMatchingPreference）
-- ============================================
-- 根据用户年龄和地区设置匹配偏好

-- 用户2：小明，1995-05-15（29岁），上海
INSERT INTO user_matching_preference (user_id, age_min, age_max, age_unlimited, distance_preference, relationship_mode_id, communication_expectation_id, additional_requirements) VALUES
(2, 24, 32, FALSE, 'same_city_or_remote', 2, 2, '希望对方有共同的兴趣爱好，能一起探索新事物');

-- 用户3：小红，1998-08-20（26岁），广州
INSERT INTO user_matching_preference (user_id, age_min, age_max, age_unlimited, distance_preference, relationship_mode_id, communication_expectation_id, additional_requirements) VALUES
(3, 24, 30, FALSE, 'same_city', 1, 1, '喜欢日常分享，希望对方能及时回复消息');

-- 用户4：小丽，1996-03-10（28岁），深圳
INSERT INTO user_matching_preference (user_id, age_min, age_max, age_unlimited, distance_preference, relationship_mode_id, communication_expectation_id, additional_requirements) VALUES
(4, 26, 32, FALSE, 'same_city_or_remote', 2, 2, '希望对方也热爱运动和阅读，能一起成长');

-- 用户5：大卫，1993-11-25（31岁），杭州
INSERT INTO user_matching_preference (user_id, age_min, age_max, age_unlimited, distance_preference, relationship_mode_id, communication_expectation_id, additional_requirements) VALUES
(5, 26, 35, FALSE, 'same_city_or_remote', 2, 3, '希望对方理解创业者的忙碌，能支持我的事业');

-- 用户6：莉莉，1997-07-07（27岁），成都
INSERT INTO user_matching_preference (user_id, age_min, age_max, age_unlimited, distance_preference, relationship_mode_id, communication_expectation_id, additional_requirements) VALUES
(6, 25, 32, FALSE, 'same_city', 1, 1, '希望对方也喜欢小动物，有爱心');

-- 用户7：Alex，1994-02-14（30岁），北京
INSERT INTO user_matching_preference (user_id, age_min, age_max, age_unlimited, distance_preference, relationship_mode_id, communication_expectation_id, additional_requirements) VALUES
(7, 26, 34, FALSE, 'same_city_or_remote', 2, 2, '希望对方有独立思考能力，能进行深度交流');

-- 用户8：Sarah，1999-06-30（25岁），上海
INSERT INTO user_matching_preference (user_id, age_min, age_max, age_unlimited, distance_preference, relationship_mode_id, communication_expectation_id, additional_requirements) VALUES
(8, 23, 30, FALSE, 'same_city', 1, 1, '希望对方也热爱生活，能一起探索城市');

-- 用户9：Mike，1992-09-18（32岁），深圳
INSERT INTO user_matching_preference (user_id, age_min, age_max, age_unlimited, distance_preference, relationship_mode_id, communication_expectation_id, additional_requirements) VALUES
(9, 26, 35, FALSE, 'same_city_or_remote', 3, 2, '希望对方理解技术工作者的节奏，能互相尊重空间');

-- 用户10：Emily，1996-12-05（28岁），杭州
INSERT INTO user_matching_preference (user_id, age_min, age_max, age_unlimited, distance_preference, relationship_mode_id, communication_expectation_id, additional_requirements) VALUES
(10, 26, 32, FALSE, 'same_city_or_remote', 2, 2, '希望对方也喜欢阅读和教育，能一起学习成长');

-- 用户11：Tom，1991-04-22（33岁），广州
INSERT INTO user_matching_preference (user_id, age_min, age_max, age_unlimited, distance_preference, relationship_mode_id, communication_expectation_id, additional_requirements) VALUES
(11, 26, 36, FALSE, 'same_city', 1, 1, '希望对方也喜欢旅行和社交，能一起探索世界');

-- 用户12：Lisa，1998-10-11（26岁），成都
INSERT INTO user_matching_preference (user_id, age_min, age_max, age_unlimited, distance_preference, relationship_mode_id, communication_expectation_id, additional_requirements) VALUES
(12, 24, 30, FALSE, 'same_city', 2, 2, '希望对方也重视健康，能一起运动');

-- 用户13：James，1995-07-28（29岁），西安
INSERT INTO user_matching_preference (user_id, age_min, age_max, age_unlimited, distance_preference, relationship_mode_id, communication_expectation_id, additional_requirements) VALUES
(13, 26, 34, FALSE, 'same_city_or_remote', 2, 2, '希望对方有独立思考能力，能进行深度讨论');

-- 用户14：Anna，1997-03-15（27岁），武汉
INSERT INTO user_matching_preference (user_id, age_min, age_max, age_unlimited, distance_preference, relationship_mode_id, communication_expectation_id, additional_requirements) VALUES
(14, 25, 32, FALSE, 'same_city_or_remote', 1, 2, '希望对方也喜欢电影和音乐，能一起分享感受');

-- 用户15：Jack，1994-11-08（30岁），南京
INSERT INTO user_matching_preference (user_id, age_min, age_max, age_unlimited, distance_preference, relationship_mode_id, communication_expectation_id, additional_requirements) VALUES
(15, 24, 32, FALSE, 'same_city', 4, 2, '希望对方也喜欢游戏，能一起开黑');

-- ============================================
-- 5. 插入用户匹配必须维度（UserMatchingMustDimension）
-- ============================================
-- 每个用户选择1-2个必须满足的维度

-- 用户2：小明 - 必须：三观一致、性格特质契合
INSERT INTO user_matching_must_dimension (user_id, dimension_id) VALUES
(2, 4), -- 性格特质契合
(2, 6); -- 沟通风格匹配

-- 用户3：小红 - 必须：关系模式一致
INSERT INTO user_matching_must_dimension (user_id, dimension_id) VALUES
(3, 5); -- 关系模式一致

-- 用户4：小丽 - 必须：性格特质契合、关系模式一致
INSERT INTO user_matching_must_dimension (user_id, dimension_id) VALUES
(4, 4), -- 性格特质契合
(4, 5); -- 关系模式一致

-- 用户5：大卫 - 必须：关系模式一致
INSERT INTO user_matching_must_dimension (user_id, dimension_id) VALUES
(5, 5); -- 关系模式一致

-- 用户6：莉莉 - 必须：兴趣重合度
INSERT INTO user_matching_must_dimension (user_id, dimension_id) VALUES
(6, 3); -- 兴趣重合度

-- 用户7：Alex - 必须：性格特质契合
INSERT INTO user_matching_must_dimension (user_id, dimension_id) VALUES
(7, 4); -- 性格特质契合

-- 用户8：Sarah - 必须：关系模式一致
INSERT INTO user_matching_must_dimension (user_id, dimension_id) VALUES
(8, 5); -- 关系模式一致

-- 用户9：Mike - 必须：关系模式一致
INSERT INTO user_matching_must_dimension (user_id, dimension_id) VALUES
(9, 5); -- 关系模式一致

-- 用户10：Emily - 必须：兴趣重合度、性格特质契合
INSERT INTO user_matching_must_dimension (user_id, dimension_id) VALUES
(10, 3), -- 兴趣重合度
(10, 4); -- 性格特质契合

-- 用户11：Tom - 必须：关系模式一致
INSERT INTO user_matching_must_dimension (user_id, dimension_id) VALUES
(11, 5); -- 关系模式一致

-- 用户12：Lisa - 必须：性格特质契合
INSERT INTO user_matching_must_dimension (user_id, dimension_id) VALUES
(12, 4); -- 性格特质契合

-- 用户13：James - 必须：性格特质契合
INSERT INTO user_matching_must_dimension (user_id, dimension_id) VALUES
(13, 4); -- 性格特质契合

-- 用户14：Anna - 必须：兴趣重合度
INSERT INTO user_matching_must_dimension (user_id, dimension_id) VALUES
(14, 3); -- 兴趣重合度

-- 用户15：Jack - 必须：兴趣重合度
INSERT INTO user_matching_must_dimension (user_id, dimension_id) VALUES
(15, 3); -- 兴趣重合度

-- ============================================
-- 6. 插入用户匹配优先维度（UserMatchingPriorityDimension）
-- ============================================
-- 每个用户选择2-3个优先考虑的维度，设置优先级顺序

-- 用户2：小明 - 优先：兴趣重合度、性格特质契合、沟通风格匹配
INSERT INTO user_matching_priority_dimension (user_id, dimension_id, priority_order) VALUES
(2, 3, 1), -- 兴趣重合度（优先级1）
(2, 4, 2), -- 性格特质契合（优先级2）
(2, 6, 3); -- 沟通风格匹配（优先级3）

-- 用户3：小红 - 优先：关系模式一致、沟通风格匹配、兴趣重合度
INSERT INTO user_matching_priority_dimension (user_id, dimension_id, priority_order) VALUES
(3, 5, 1), -- 关系模式一致（优先级1）
(3, 6, 2), -- 沟通风格匹配（优先级2）
(3, 3, 3); -- 兴趣重合度（优先级3）

-- 用户4：小丽 - 优先：性格特质契合、关系模式一致、兴趣重合度
INSERT INTO user_matching_priority_dimension (user_id, dimension_id, priority_order) VALUES
(4, 4, 1), -- 性格特质契合（优先级1）
(4, 5, 2), -- 关系模式一致（优先级2）
(4, 3, 3); -- 兴趣重合度（优先级3）

-- 用户5：大卫 - 优先：关系模式一致、性格特质契合、沟通风格匹配
INSERT INTO user_matching_priority_dimension (user_id, dimension_id, priority_order) VALUES
(5, 5, 1), -- 关系模式一致（优先级1）
(5, 4, 2), -- 性格特质契合（优先级2）
(5, 6, 3); -- 沟通风格匹配（优先级3）

-- 用户6：莉莉 - 优先：兴趣重合度、性格特质契合、关系模式一致
INSERT INTO user_matching_priority_dimension (user_id, dimension_id, priority_order) VALUES
(6, 3, 1), -- 兴趣重合度（优先级1）
(6, 4, 2), -- 性格特质契合（优先级2）
(6, 5, 3); -- 关系模式一致（优先级3）

-- 用户7：Alex - 优先：性格特质契合、兴趣重合度、沟通风格匹配
INSERT INTO user_matching_priority_dimension (user_id, dimension_id, priority_order) VALUES
(7, 4, 1), -- 性格特质契合（优先级1）
(7, 3, 2), -- 兴趣重合度（优先级2）
(7, 6, 3); -- 沟通风格匹配（优先级3）

-- 用户8：Sarah - 优先：关系模式一致、兴趣重合度、沟通风格匹配
INSERT INTO user_matching_priority_dimension (user_id, dimension_id, priority_order) VALUES
(8, 5, 1), -- 关系模式一致（优先级1）
(8, 3, 2), -- 兴趣重合度（优先级2）
(8, 6, 3); -- 沟通风格匹配（优先级3）

-- 用户9：Mike - 优先：关系模式一致、性格特质契合、沟通风格匹配
INSERT INTO user_matching_priority_dimension (user_id, dimension_id, priority_order) VALUES
(9, 5, 1), -- 关系模式一致（优先级1）
(9, 4, 2), -- 性格特质契合（优先级2）
(9, 6, 3); -- 沟通风格匹配（优先级3）

-- 用户10：Emily - 优先：兴趣重合度、性格特质契合、关系模式一致
INSERT INTO user_matching_priority_dimension (user_id, dimension_id, priority_order) VALUES
(10, 3, 1), -- 兴趣重合度（优先级1）
(10, 4, 2), -- 性格特质契合（优先级2）
(10, 5, 3); -- 关系模式一致（优先级3）

-- 用户11：Tom - 优先：关系模式一致、兴趣重合度、沟通风格匹配
INSERT INTO user_matching_priority_dimension (user_id, dimension_id, priority_order) VALUES
(11, 5, 1), -- 关系模式一致（优先级1）
(11, 3, 2), -- 兴趣重合度（优先级2）
(11, 6, 3); -- 沟通风格匹配（优先级3）

-- 用户12：Lisa - 优先：性格特质契合、关系模式一致、兴趣重合度
INSERT INTO user_matching_priority_dimension (user_id, dimension_id, priority_order) VALUES
(12, 4, 1), -- 性格特质契合（优先级1）
(12, 5, 2), -- 关系模式一致（优先级2）
(12, 3, 3); -- 兴趣重合度（优先级3）

-- 用户13：James - 优先：性格特质契合、沟通风格匹配、关系模式一致
INSERT INTO user_matching_priority_dimension (user_id, dimension_id, priority_order) VALUES
(13, 4, 1), -- 性格特质契合（优先级1）
(13, 6, 2), -- 沟通风格匹配（优先级2）
(13, 5, 3); -- 关系模式一致（优先级3）

-- 用户14：Anna - 优先：兴趣重合度、关系模式一致、沟通风格匹配
INSERT INTO user_matching_priority_dimension (user_id, dimension_id, priority_order) VALUES
(14, 3, 1), -- 兴趣重合度（优先级1）
(14, 5, 2), -- 关系模式一致（优先级2）
(14, 6, 3); -- 沟通风格匹配（优先级3）

-- 用户15：Jack - 优先：兴趣重合度、关系模式一致、沟通风格匹配
INSERT INTO user_matching_priority_dimension (user_id, dimension_id, priority_order) VALUES
(15, 3, 1), -- 兴趣重合度（优先级1）
(15, 5, 2), -- 关系模式一致（优先级2）
(15, 6, 3); -- 沟通风格匹配（优先级3）

-- ============================================
-- 7. 插入用户问卷完成记录（UserQuestionnaireCompletion）
-- ============================================
INSERT INTO user_questionnaire_completion (user_id, first_completed_at, last_submitted_at, submission_count) VALUES
(2, NOW(), NOW(), 1),
(3, NOW(), NOW(), 1),
(4, NOW(), NOW(), 1),
(5, NOW(), NOW(), 1),
(6, NOW(), NOW(), 1),
(7, NOW(), NOW(), 1),
(8, NOW(), NOW(), 1),
(9, NOW(), NOW(), 1),
(10, NOW(), NOW(), 1),
(11, NOW(), NOW(), 1),
(12, NOW(), NOW(), 1),
(13, NOW(), NOW(), 1),
(14, NOW(), NOW(), 1),
(15, NOW(), NOW(), 1);

-- ============================================
-- 8. 更新用户表的问卷完成状态
-- ============================================
UPDATE user 
SET matching_questionnaire_completed = TRUE,
    matching_questionnaire_completed_at = NOW()
WHERE user_id IN (2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

-- 输出完成信息
SELECT '匹配用户数据插入完成！' AS '状态';

