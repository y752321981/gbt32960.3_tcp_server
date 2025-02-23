/*
 Navicat Premium Dump SQL

 Source Server         : 服务器
 Source Server Type    : MySQL
 Source Server Version : 80027 (8.0.27)
 Source Host           : 192.168.31.71:3306
 Source Schema         : gbt32960

 Target Server Type    : MySQL
 Target Server Version : 80027 (8.0.27)
 File Encoding         : 65001

 Date: 23/02/2025 15:45:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for origin_record
-- ----------------------------
DROP TABLE IF EXISTS `origin_record`;
CREATE TABLE `origin_record`  (
  `id` bigint NOT NULL COMMENT '主键',
  `origin` blob NOT NULL COMMENT '原始数据',
  `vin` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '解析出的车辆VIN号',
  `time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `vin_time_index`(`vin` ASC, `time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '原始数据记录' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
