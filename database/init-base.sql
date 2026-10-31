-- MySQL8空白业务库初始化；请先选择flower_mall库并导入schema.sql。
SET NAMES utf8mb4;
INSERT INTO category (id,name) VALUES (1,'浪漫告白');
INSERT INTO category (id,name) VALUES (2,'生日惊喜');
INSERT INTO category (id,name) VALUES (3,'温暖问候');
INSERT INTO category (id,name) VALUES (4,'毕业季');
INSERT INTO category (id,name) VALUES (5,'开业庆典');
INSERT INTO category (id,name) VALUES (6,'日常悦己');
INSERT INTO flower_language (id,name,image,meaning,colors,culture,scenes) VALUES (1,'玫瑰','/images/bouquet-1.svg','热爱、真挚与浪漫','红色：热烈爱意；粉色：温柔感谢；白色：纯洁真挚。','玫瑰常见于节日赠礼、生活装饰与文学表达。花语来自长期形成的文化约定，不同地区、文化和关系背景可能赋予它不同含义；赠花时应结合对方喜好表达心意。','告白、纪念日');
INSERT INTO flower_language (id,name,image,meaning,colors,culture,scenes) VALUES (2,'郁金香','/images/bouquet-2.svg','美好祝愿与优雅','粉色：珍惜祝福；黄色：明朗友谊；紫色：高雅神秘。','郁金香常见于节日赠礼、生活装饰与文学表达。花语来自长期形成的文化约定，不同地区、文化和关系背景可能赋予它不同含义；赠花时应结合对方喜好表达心意。','生日、日常悦己');
INSERT INTO flower_language (id,name,image,meaning,colors,culture,scenes) VALUES (3,'百合','/images/bouquet-3.svg','纯洁、美好与百年好合','白色：纯洁祝愿；粉色：温柔幸福；黄色：感激喜悦。','百合常见于节日赠礼、生活装饰与文学表达。花语来自长期形成的文化约定，不同地区、文化和关系背景可能赋予它不同含义；赠花时应结合对方喜好表达心意。','婚礼、祝福');
INSERT INTO flower_language (id,name,image,meaning,colors,culture,scenes) VALUES (4,'康乃馨','/images/bouquet-4.svg','感恩、亲情与温柔','粉色：感恩母爱；红色：敬爱祝福；白色：纯洁思念。','康乃馨常见于节日赠礼、生活装饰与文学表达。花语来自长期形成的文化约定，不同地区、文化和关系背景可能赋予它不同含义；赠花时应结合对方喜好表达心意。','母亲节、感谢');
INSERT INTO flower_language (id,name,image,meaning,colors,culture,scenes) VALUES (5,'向日葵','/images/bouquet-5.svg','阳光、信念与向往','金黄色：向阳而生，坚定与热情。','向日葵常见于节日赠礼、生活装饰与文学表达。花语来自长期形成的文化约定，不同地区、文化和关系背景可能赋予它不同含义；赠花时应结合对方喜好表达心意。','毕业、探望');
INSERT INTO flower_language (id,name,image,meaning,colors,culture,scenes) VALUES (6,'满天星','/images/bouquet-6.svg','陪伴、纯真与思念','白色：纯真陪伴；粉色：温柔心意；蓝色：宁静守候（常见染色花材）。','满天星常见于节日赠礼、生活装饰与文学表达。花语来自长期形成的文化约定，不同地区、文化和关系背景可能赋予它不同含义；赠花时应结合对方喜好表达心意。','陪伴、搭配赠花');
INSERT INTO flower_language (id,name,image,meaning,colors,culture,scenes) VALUES (7,'薰衣草','/images/bouquet-1.svg','宁静、守候与期待','紫色：宁静与等待，浪漫与回忆。','薰衣草常见于节日赠礼、生活装饰与文学表达。花语来自长期形成的文化约定，不同地区、文化和关系背景可能赋予它不同含义；赠花时应结合对方喜好表达心意。','日常、感谢');
INSERT INTO flower_language (id,name,image,meaning,colors,culture,scenes) VALUES (8,'牡丹','/images/bouquet-2.svg','富贵、圆满与繁荣','粉色：圆满美好；红色：繁荣富贵；白色：高洁。','牡丹常见于节日赠礼、生活装饰与文学表达。花语来自长期形成的文化约定，不同地区、文化和关系背景可能赋予它不同含义；赠花时应结合对方喜好表达心意。','开业、新居');
INSERT INTO flower_language (id,name,image,meaning,colors,culture,scenes) VALUES (9,'绣球','/images/bouquet-3.svg','团聚、希望与珍惜','蓝色：宁静与理解；粉色：浪漫与幸福；白色：希望。','绣球常见于节日赠礼、生活装饰与文学表达。花语来自长期形成的文化约定，不同地区、文化和关系背景可能赋予它不同含义；赠花时应结合对方喜好表达心意。','团聚、纪念日');
INSERT INTO flower_language (id,name,image,meaning,colors,culture,scenes) VALUES (10,'非洲菊','/images/bouquet-4.svg','热情、活力与快乐','橙色：活力；黄色：快乐；粉色：温柔关怀。','非洲菊常见于节日赠礼、生活装饰与文学表达。花语来自长期形成的文化约定，不同地区、文化和关系背景可能赋予它不同含义；赠花时应结合对方喜好表达心意。','生日、开业');
