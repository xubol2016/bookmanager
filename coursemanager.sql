/*
 Source Server         : MySQL
 Source Server Type    : MySQL
 Target Server Type    : MySQL
 File Encoding         : 65001
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Create Database
-- ----------------------------
CREATE DATABASE IF NOT EXISTS `coursemanager` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `coursemanager`;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '加密后的密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `role` int(4) NOT NULL DEFAULT 0 COMMENT '角色: 0-普通用户, 1-管理员',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `status` int(4) NOT NULL DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- Records of users
-- ----------------------------
BEGIN;
-- Admin user: password '123456' (Note: In a real app this should be BCrypt, using plain/md5 placeholder here for demo or assuming app handles hashing)
INSERT INTO `users` VALUES (1, 'admin', '123456', '系统管理员', 1, '13800138000', 1, '2023-01-01 10:00:00');
INSERT INTO `users` VALUES (2, 'zhangsan', '123456', '张三', 0, '13900000001', 1, '2023-01-02 10:00:00');
INSERT INTO `users` VALUES (3, 'lisi', '123456', '李四', 0, '13900000002', 1, '2023-01-03 12:00:00');
INSERT INTO `users` VALUES (4, 'wangwu', '123456', '王五', 0, '13900000003', 1, '2023-01-04 14:00:00');
INSERT INTO `users` VALUES (5, 'zhaoliu', '123456', '赵六', 0, '13900000004', 1, '2023-01-05 09:30:00');
INSERT INTO `users` VALUES (6, 'qianqi', '123456', '钱七', 0, '13900000005', 0, '2023-01-06 15:20:00');
COMMIT;

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `parent_id` bigint(20) DEFAULT 0 COMMENT '父级ID',
  `level` int(4) DEFAULT 1 COMMENT '层级深度',
  `sort_order` int(4) DEFAULT 0 COMMENT '排序优先级',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

-- ----------------------------
-- Records of categories
-- ----------------------------
BEGIN;
INSERT INTO `categories` VALUES (1, '计算机科学', 0, 1, 1);
INSERT INTO `categories` VALUES (2, '文学', 0, 1, 2);
INSERT INTO `categories` VALUES (3, '历史', 0, 1, 3);
INSERT INTO `categories` VALUES (4, '编程语言', 1, 2, 1);
INSERT INTO `categories` VALUES (5, '数据库', 1, 2, 2);
INSERT INTO `categories` VALUES (6, '中国文学', 2, 2, 1);
INSERT INTO `categories` VALUES (7, '外国文学', 2, 2, 2);
INSERT INTO `categories` VALUES (8, '中国历史', 3, 2, 1);
COMMIT;

-- ----------------------------
-- Table structure for books
-- ----------------------------
DROP TABLE IF EXISTS `books`;
CREATE TABLE `books` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) NOT NULL COMMENT '图书名称',
  `author` varchar(50) NOT NULL COMMENT '作者',
  `publisher` varchar(50) DEFAULT NULL COMMENT '出版社',
  `isbn` varchar(20) NOT NULL COMMENT 'ISBN编码',
  `cover_url` varchar(255) DEFAULT NULL COMMENT '封面图片地址',
  `category_id` bigint(20) DEFAULT NULL COMMENT '关联分类ID',
  `stock` int(11) DEFAULT 0 COMMENT '当前库存数量',
  `location` varchar(50) DEFAULT NULL COMMENT '存放位置',
  `description` varchar(500) DEFAULT NULL COMMENT '图书简介',
  `status` int(4) DEFAULT 1 COMMENT '状态: 1-上架, 0-下架',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`),
  CONSTRAINT `fk_book_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- ----------------------------
-- Records of books
-- ----------------------------
BEGIN;
INSERT INTO `books` VALUES (1, 'Java编程思想', 'Bruce Eckel', '机械工业出版社', '9787111213826', NULL, 4, 10, 'A-01-01', 'Java经典著作', 1);
INSERT INTO `books` VALUES (2, '深入理解Java虚拟机', '周志明', '机械工业出版社', '9787111636663', NULL, 4, 15, 'A-01-02', 'JVM必读', 1);
INSERT INTO `books` VALUES (3, 'MySQL必知必会', 'Ben Forta', '人民邮电出版社', '9787115191120', NULL, 5, 20, 'A-02-01', '数据库入门', 1);
INSERT INTO `books` VALUES (4, '高性能MySQL', 'Baron Schwartz', '电子工业出版社', '9787121198854', NULL, 5, 8, 'A-02-02', 'DBA进阶', 1);
INSERT INTO `books` VALUES (5, '红楼梦', '曹雪芹', '人民文学出版社', '9787020002207', NULL, 6, 30, 'B-01-01', '四大名著之一', 1);
INSERT INTO `books` VALUES (6, '百年孤独', '加西亚·马尔克斯', '南海出版公司', '9787544253994', NULL, 7, 12, 'B-02-01', '魔幻现实主义', 1);
INSERT INTO `books` VALUES (7, '万历十五年', '黄仁宇', '中华书局', '9787101052039', NULL, 8, 18, 'C-01-01', '明史研究', 1);
INSERT INTO `books` VALUES (8, '平凡的世界', '路遥', '北京十月文艺出版社', '9787530219218', NULL, 6, 25, 'B-01-02', '茅盾文学奖作品', 1);
COMMIT;

-- ----------------------------
-- Table structure for borrow_records
-- ----------------------------
DROP TABLE IF EXISTS `borrow_records`;
CREATE TABLE `borrow_records` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `book_id` bigint(20) NOT NULL COMMENT '图书ID',
  `borrow_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '借阅时间',
  `due_time` datetime DEFAULT NULL COMMENT '应还时间',
  `return_time` datetime DEFAULT NULL COMMENT '实际归还时间',
  `status` int(4) DEFAULT 1 COMMENT '状态: 1-借阅中, 2-已归还, 3-已超期',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_book` (`book_id`),
  CONSTRAINT `fk_record_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_record_book` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

-- ----------------------------
-- Records of borrow_records
-- ----------------------------
BEGIN;
INSERT INTO `borrow_records` VALUES (1, 2, 1, '2023-05-01 10:00:00', '2023-06-01 10:00:00', '2023-05-20 14:00:00', 2, '提前归还');
INSERT INTO `borrow_records` VALUES (2, 2, 5, '2023-06-05 11:00:00', '2023-07-05 11:00:00', NULL, 1, NULL);
INSERT INTO `borrow_records` VALUES (3, 3, 2, '2023-06-10 09:00:00', '2023-07-10 09:00:00', NULL, 1, NULL);
INSERT INTO `borrow_records` VALUES (4, 4, 3, '2023-03-01 08:00:00', '2023-04-01 08:00:00', NULL, 3, '已超期未还');
INSERT INTO `borrow_records` VALUES (5, 5, 6, '2023-06-15 15:30:00', '2023-07-15 15:30:00', NULL, 1, NULL);
INSERT INTO `borrow_records` VALUES (6, 3, 7, '2023-05-20 10:00:00', '2023-06-20 10:00:00', '2023-06-18 16:00:00', 2, NULL);
COMMIT;

-- ----------------------------
-- Table structure for announcements
-- ----------------------------
DROP TABLE IF EXISTS `announcements`;
CREATE TABLE `announcements` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) NOT NULL COMMENT '公告标题',
  `content` text COMMENT '公告内容',
  `admin_id` bigint(20) NOT NULL COMMENT '发布管理员ID',
  `publish_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `is_top` int(4) DEFAULT 0 COMMENT '是否置顶: 1-是, 0-否',
  PRIMARY KEY (`id`),
  KEY `idx_admin` (`admin_id`),
  CONSTRAINT `fk_announce_admin` FOREIGN KEY (`admin_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';

-- ----------------------------
-- Records of announcements
-- ----------------------------
BEGIN;
INSERT INTO `announcements` VALUES (1, '系统上线通知', '图书管理系统正式上线运行，欢迎各位师生使用。', 1, '2023-01-01 08:00:00', 1);
INSERT INTO `announcements` VALUES (2, '关于图书馆闭馆维护的通知', '本周日图书馆将进行电路维护，闭馆一天，请相互转告。', 1, '2023-05-10 09:00:00', 0);
INSERT INTO `announcements` VALUES (3, '新书上架：计算机类', '近期上架了一批高性能计算相关的专业书籍，欢迎借阅。', 1, '2023-06-01 10:00:00', 0);
INSERT INTO `announcements` VALUES (4, '端午节放假安排', '端午假期间图书馆正常开放，开放时间调整为9:00-17:00。', 1, '2023-06-20 08:30:00', 0);
INSERT INTO `announcements` VALUES (5, '暑期借阅规则调整', '暑假期间借阅期限延长至60天。', 1, '2023-07-01 08:00:00', 1);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
