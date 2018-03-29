/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50626
 Source Host           : localhost
 Source Database       : deploy

 Target Server Type    : MySQL
 Target Server Version : 50626
 File Encoding         : utf-8

 Date: 03/27/2018 15:24:07 PM
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `t_account`
-- ----------------------------
DROP TABLE IF EXISTS `t_account`;
CREATE TABLE `t_account` (
  `uid` bigint(11) NOT NULL DEFAULT '0' COMMENT '主键',
  `password` varchar(64) NOT NULL COMMENT '密码',
  `account` varchar(50) NOT NULL COMMENT '贝聊账号',
  `operator` int(11) NOT NULL DEFAULT '0' COMMENT '操作人id',
  `realname` varchar(50) NOT NULL COMMENT '真名',
  `mobile_no` varchar(20) NOT NULL DEFAULT '' COMMENT '电话号码',
  `last_modify` datetime NOT NULL COMMENT '修改时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `account_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '用户状态，1-正常,2-冻结',
  `default_data` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否是基本用户( 可以理解为是否是管理员)，0-不是，1-是,不可以删除',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
--  Records of `t_account`
-- ----------------------------
BEGIN;
INSERT INTO `t_account` VALUES ('1', 'e10adc3949ba59abbe56e057f20f883e', 'lgl', '1', '梁广龙', '15521110047', '2018-03-26 20:59:56', '2018-03-26 20:59:59', '1', '1');
COMMIT;

-- ----------------------------
--  Table structure for `t_account_role_relation`
-- ----------------------------
DROP TABLE IF EXISTS `t_account_role_relation`;
CREATE TABLE `t_account_role_relation` (
  `uid` bigint(11) NOT NULL COMMENT ' 用户id',
  `role_id` int(11) NOT NULL COMMENT '角色id',
  PRIMARY KEY (`uid`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户与角色对应关系';

-- ----------------------------
--  Table structure for `t_admin_operation_log`
-- ----------------------------
DROP TABLE IF EXISTS `t_admin_operation_log`;
CREATE TABLE `t_admin_operation_log` (
  `log_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uid` bigint(20) NOT NULL COMMENT '管理员ID',
  `operation_type` varchar(30) NOT NULL COMMENT '操作类型',
  `arguments` varchar(512) NOT NULL DEFAULT '' COMMENT '参数',
  `result` varchar(255) NOT NULL DEFAULT '' COMMENT '执行结果',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `ext1` varchar(64) NOT NULL DEFAULT '' COMMENT '扩展字段1',
  `ext2` varchar(64) NOT NULL DEFAULT '' COMMENT '扩展字段2',
  PRIMARY KEY (`log_id`),
  KEY `idx_account_time` (`uid`,`create_time`),
  KEY `idx_time_operation_type` (`create_time`,`operation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='管理员操作日志';

-- ----------------------------
--  Table structure for `t_app_define`
-- ----------------------------
DROP TABLE IF EXISTS `t_app_define`;
CREATE TABLE `t_app_define` (
  `app_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '流水号',
  `app_name` varchar(30) NOT NULL COMMENT '名称',
  `display_name` varchar(30) NOT NULL COMMENT '用于显示的名称',
  `app_key` varchar(80) NOT NULL COMMENT '系统连接的密钥',
  `summary` varchar(255) NOT NULL COMMENT '系统描述',
  `app_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否可用,1可用，2不可用',
  `login_url` varchar(200) NOT NULL COMMENT '切换系统的登录url',
  `sequence` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `operator` int(11) NOT NULL COMMENT '操作者',
  `last_modify` datetime NOT NULL COMMENT '更新时间',
  `bug_report_url` varchar(200) NOT NULL DEFAULT '#' COMMENT '报bug url',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`app_id`),
  UNIQUE KEY `uniq_Key_app_name` (`app_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='应用系统信息';

-- ----------------------------
--  Table structure for `t_deploy_history`
-- ----------------------------
DROP TABLE IF EXISTS `t_deploy_history`;
CREATE TABLE `t_deploy_history` (
  `history_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '版本发布历史ID',
  `title` varchar(80) NOT NULL COMMENT '发布版本的标题',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `account_id` int(11) NOT NULL COMMENT '发布者id',
  `deploy_time` datetime DEFAULT NULL COMMENT '发布时间',
  `result` tinyint(4) NOT NULL COMMENT '1 是全部成功 2是部分成功 3是全部失败',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `auditor_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '审核人ID',
  `tag_name` varchar(60) NOT NULL DEFAULT '' COMMENT '发布的tag/分支，相对地址，例如 20161025，和 t_project_config.repo_url组合起来就是完整地址。如果发布trunk，这里可以为""',
  `version_no` varchar(30) NOT NULL DEFAULT '' COMMENT '发布的版本号',
  `is_restart` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否是重启，0不是，1是',
  `is_rollback` tinyint(4) NOT NULL DEFAULT '0' COMMENT '本次发布是否是回滚操作, 0不是  1是',
  `rollback_to_deploy_id` int(11) NOT NULL DEFAULT '0' COMMENT '回滚到的版本发布历史ID',
  `module_id` int(11) NOT NULL COMMENT '哪个模块部署的',
  `module_name` varchar(80) NOT NULL COMMENT '模块名称',
  `env_id` int(11) NOT NULL COMMENT '环境',
  `project_id` int(11) NOT NULL COMMENT '项目ID',
  `deploy_servers` int(11) NOT NULL COMMENT '发布的服务器数量',
  `success_count` int(11) NOT NULL COMMENT '发布成功的服务器数量',
  `concurrent_server_percentage` tinyint(4) NOT NULL DEFAULT '0' COMMENT '并发发布服务器百分比',
  `deploy_time_interval` smallint(11) NOT NULL COMMENT '发布时间间隔,单位是秒  ',
  `deploy_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发布状态： 1代表等待审核，2代表等待部署，3代表审核拒绝，4代表发布取消，5已经发布',
  `force_compile` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否强制编译，0不 1强制编译',
  `real_name` varchar(50) NOT NULL COMMENT '发布者真实姓名',
  `server_strategy` tinyint(4) NOT NULL DEFAULT '1' COMMENT '服务器发布策略，1 是遇到失败马上停止，2是无论有没失败，都继续',
  PRIMARY KEY (`history_id`),
  KEY `idx_project_id` (`project_id`,`create_time`),
  KEY `idx_module_id` (`module_id`,`create_time`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_deployTime` (`deploy_time`) USING BTREE,
  KEY `idx_account_id` (`account_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='发布历史';

-- ----------------------------
--  Table structure for `t_global_setting`
-- ----------------------------
DROP TABLE IF EXISTS `t_global_setting`;
CREATE TABLE `t_global_setting` (
  `setting_id` int(11) NOT NULL COMMENT '主键',
  `svn_checkout_dir` varchar(200) NOT NULL COMMENT 'svn checkout 目录',
  `target_server_user` varchar(80) NOT NULL COMMENT '目标服务器用户',
  `target_server_dir` varchar(80) NOT NULL COMMENT '目标服务器目录',
  `idc` varchar(80) NOT NULL DEFAULT '' COMMENT 'idc ',
  PRIMARY KEY (`setting_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='全局配置表';

-- ----------------------------
--  Table structure for `t_low_quality_rank`
-- ----------------------------
DROP TABLE IF EXISTS `t_low_quality_rank`;
CREATE TABLE `t_low_quality_rank` (
  `rank_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `module_id` int(11) NOT NULL COMMENT '模块ID',
  `deploy_times` int(11) NOT NULL COMMENT '成功发布次数，失败的不计算在内',
  PRIMARY KEY (`rank_id`),
  UNIQUE KEY `uniq_key_stat_date_module` (`stat_date`,`module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='最差发布质量排行，只统计生产环境';

-- ----------------------------
--  Table structure for `t_menu`
-- ----------------------------
DROP TABLE IF EXISTS `t_menu`;
CREATE TABLE `t_menu` (
  `menu_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `parent_menu_id` int(11) NOT NULL DEFAULT '0' COMMENT '父菜单id',
  `app_id` int(11) NOT NULL COMMENT '应用系统id',
  `menu_name` varchar(30) NOT NULL COMMENT '名称',
  `url` varchar(200) NOT NULL COMMENT '资源',
  `sequence` int(11) NOT NULL DEFAULT '0' COMMENT '排序id号',
  `operator` bigint(20) NOT NULL DEFAULT '0' COMMENT '操作者',
  `last_modify` datetime NOT NULL COMMENT '更新时间',
  `hide` tinyint(1) NOT NULL DEFAULT '0' COMMENT '菜单是否隐藏，true隐藏,false不隐藏',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`menu_id`),
  UNIQUE KEY `uniq_Key_app_id` (`app_id`,`menu_name`)
) ENGINE=InnoDB AUTO_INCREMENT=2133326262 DEFAULT CHARSET=utf8 COMMENT='菜单';

-- ----------------------------
--  Records of `t_menu`
-- ----------------------------
BEGIN;
INSERT INTO `t_menu` VALUES ('403425958', '2133326261', '1', '发布', '', '700000', '1', '2018-03-27 15:01:41', '0', '2018-03-27 15:01:41'), ('501947879', '1447280342', '1', '低质量发布', '', '700000', '1', '2018-03-27 15:01:41', '0', '2018-03-27 15:01:41'), ('539689649', '0', '1', '全局配置管理', '', '900000', '1', '2018-03-27 15:01:41', '0', '2018-03-27 15:01:41'), ('578506625', '539689649', '1', '环境列表', '', '500036', '1', '2018-03-27 15:01:41', '0', '2018-03-27 15:01:41'), ('619747848', '0', '1', '帐号管理', '', '100000', '1', '2018-03-27 15:01:40', '0', '2018-03-27 15:01:40'), ('716708294', '2133326261', '1', '发布记录', '', '900000', '1', '2018-03-27 15:01:41', '0', '2018-03-27 15:01:41'), ('1050797901', '1375765154', '1', '项目详情', '', '500038', '1', '2018-03-27 15:01:41', '0', '2018-03-27 15:01:41'), ('1110639353', '1447280342', '1', '按项目统计', '', '800000', '1', '2018-03-27 15:01:41', '0', '2018-03-27 15:01:41'), ('1264419240', '619747848', '1', '角色列表', '', '500000', '1', '2018-03-27 15:01:40', '0', '2018-03-27 15:01:40'), ('1265630538', '1375765154', '1', '模块详情', '', '500037', '1', '2018-03-27 15:01:41', '0', '2018-03-27 15:01:41'), ('1328382829', '619747848', '1', '管理员列表', '', '400000', '1', '2018-03-27 15:01:40', '0', '2018-03-27 15:01:40'), ('1375765154', '0', '1', '项目管理', '', '1099999', '1', '2018-03-27 15:01:41', '0', '2018-03-27 15:01:41'), ('1444380220', '619747848', '1', '增加/修改角色', '', '490000', '1', '2018-03-27 15:01:40', '0', '2018-03-27 15:01:40'), ('1447280342', '0', '1', '统计', '', '500000', '1', '2018-03-27 15:01:41', '0', '2018-03-27 15:01:41'), ('1549507868', '1375765154', '1', '项目列表', '', '500039', '1', '2018-03-27 15:01:41', '0', '2018-03-27 15:01:41'), ('1634833334', '2133326261', '1', '创建上线单', '', '1000000', '1', '2018-03-27 15:01:41', '0', '2018-03-27 15:01:41'), ('1710546407', '2133326261', '1', '发布详情', '', '800000', '1', '2018-03-27 15:01:41', '0', '2018-03-27 15:01:41'), ('2027353109', '1447280342', '1', '按环境统计', '', '900000', '1', '2018-03-27 15:01:41', '0', '2018-03-27 15:01:41'), ('2064158090', '619747848', '1', '增加/修改管理员', '', '390000', '1', '2018-03-27 15:01:40', '0', '2018-03-27 15:01:40'), ('2133326261', '0', '1', '项目发布', '', '950000', '1', '2018-03-27 15:01:41', '0', '2018-03-27 15:01:41');
COMMIT;

-- ----------------------------
--  Table structure for `t_menu_resources_relation`
-- ----------------------------
DROP TABLE IF EXISTS `t_menu_resources_relation`;
CREATE TABLE `t_menu_resources_relation` (
  `menu_id` int(11) NOT NULL COMMENT '菜单id',
  `res_id` int(11) NOT NULL COMMENT '资源id',
  PRIMARY KEY (`menu_id`,`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='菜单与资源对应表';

-- ----------------------------
--  Records of `t_menu_resources_relation`
-- ----------------------------
BEGIN;
INSERT INTO `t_menu_resources_relation` VALUES ('403425958', '1508724792'), ('403425958', '1534282863'), ('501947879', '590675340'), ('501947879', '1095019264'), ('578506625', '578506625'), ('578506625', '1542090117'), ('578506625', '2131415867'), ('716708294', '102820637'), ('716708294', '614414224'), ('716708294', '723930349'), ('716708294', '931031267'), ('716708294', '1114507269'), ('716708294', '1392474487'), ('716708294', '1609851906'), ('716708294', '2093356949'), ('1050797901', '244718962'), ('1050797901', '405161938'), ('1050797901', '1050797901'), ('1050797901', '1705677228'), ('1050797901', '1787261272'), ('1050797901', '1875555807'), ('1110639353', '418198772'), ('1110639353', '1495227657'), ('1264419240', '616097641'), ('1264419240', '1264419240'), ('1264419240', '1574603878'), ('1265630538', '101325062'), ('1265630538', '972136918'), ('1265630538', '1558663308'), ('1265630538', '1942032933'), ('1265630538', '2012793559'), ('1328382829', '29453733'), ('1328382829', '816462416'), ('1328382829', '1298862981'), ('1444380220', '286952581'), ('1444380220', '537609575'), ('1444380220', '615557575'), ('1549507868', '744239864'), ('1549507868', '1549507868'), ('1549507868', '2080419363'), ('1634833334', '225384434'), ('1634833334', '833599740'), ('1634833334', '1556062695'), ('1634833334', '1634833334'), ('1710546407', '540530756'), ('1710546407', '1710546407'), ('1710546407', '1746249879'), ('2027353109', '561626920'), ('2027353109', '1199879837'), ('2064158090', '740189465'), ('2064158090', '1056731337'), ('2064158090', '1854899268');
COMMIT;

-- ----------------------------
--  Table structure for `t_module_conf`
-- ----------------------------
DROP TABLE IF EXISTS `t_module_conf`;
CREATE TABLE `t_module_conf` (
  `module_id` int(11) NOT NULL COMMENT '模块ID',
  `conf_type` int(11) NOT NULL COMMENT '配置类型,1-resin, 2-tomcat, 3-其他',
  `conf_value` varchar(512) NOT NULL COMMENT '配置，json格式',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`module_id`,`conf_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模块配置表';

-- ----------------------------
--  Table structure for `t_module_jvm`
-- ----------------------------
DROP TABLE IF EXISTS `t_module_jvm`;
CREATE TABLE `t_module_jvm` (
  `module_jvm_id` int(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `module_id` int(20) NOT NULL COMMENT '模块id',
  `env_id` int(20) NOT NULL COMMENT '环境id',
  `env_name` varchar(50) NOT NULL COMMENT '环境名称',
  `jvm_args` varchar(255) NOT NULL DEFAULT '' COMMENT 'jvm 参数',
  PRIMARY KEY (`module_jvm_id`),
  KEY `idx_moduleId` (`module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='模块在每个环境的jvm参数';

-- ----------------------------
--  Table structure for `t_project`
-- ----------------------------
DROP TABLE IF EXISTS `t_project`;
CREATE TABLE `t_project` (
  `project_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_name` varchar(60) NOT NULL COMMENT '项目名称，全局唯一，不能重名',
  `manager_id` int(11) NOT NULL COMMENT '项目管理员ID',
  `manager_name` varchar(40) NOT NULL DEFAULT '' COMMENT '管理员名称',
  `manager_email` varchar(60) NOT NULL DEFAULT '' COMMENT '管理员email',
  `manager_phone` varchar(20) NOT NULL DEFAULT '' COMMENT '管理员电话',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `project_no` varchar(50) NOT NULL COMMENT '项目编号',
  `program_language` varchar(20) NOT NULL COMMENT '项目的后台编程语言',
  PRIMARY KEY (`project_id`),
  UNIQUE KEY `uniq_projectName` (`project_name`) USING BTREE,
  UNIQUE KEY `uniq_projectNo` (`project_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目';

-- ----------------------------
--  Table structure for `t_project_account_relation`
-- ----------------------------
DROP TABLE IF EXISTS `t_project_account_relation`;
CREATE TABLE `t_project_account_relation` (
  `project_id` int(11) NOT NULL COMMENT '项目ID',
  `account_id` bigint(11) NOT NULL COMMENT '用户ID',
  `is_admin` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否为管理员，0不是，1是 ，一个项目至少有一个管理员',
  PRIMARY KEY (`project_id`,`account_id`),
  KEY `idx_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目和成员的关系表';

-- ----------------------------
--  Table structure for `t_project_env`
-- ----------------------------
DROP TABLE IF EXISTS `t_project_env`;
CREATE TABLE `t_project_env` (
  `env_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `env_name` varchar(40) NOT NULL COMMENT '环境名称，dev test pre online',
  `online_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否是线上环境，0不是，1 是',
  PRIMARY KEY (`env_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='环境配置表';

-- ----------------------------
--  Table structure for `t_project_module`
-- ----------------------------
DROP TABLE IF EXISTS `t_project_module`;
CREATE TABLE `t_project_module` (
  `module_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `module_name_zh` varchar(40) NOT NULL COMMENT '模块中文名称',
  `module_name` varchar(80) NOT NULL COMMENT '模块名称',
  `module_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '模块类型，1代表web项目 2代表dubbo服务',
  `src_path` varchar(200) NOT NULL DEFAULT '' COMMENT 'SVN上的目录，比如 service-impl/target/*.jar',
  `pre_shell` varchar(500) NOT NULL DEFAULT '' COMMENT '发布前执行的shell',
  `post_shell` varchar(500) NOT NULL DEFAULT '' COMMENT '发布后执行的shell',
  `log_name` varchar(200) NOT NULL DEFAULT '' COMMENT '日志名称，读取日志并返回',
  `repo_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '版本管理类型, 1-svn, 2-git',
  `repo_url` varchar(200) NOT NULL DEFAULT '' COMMENT '版本管理地址，比如：svn://a.b.com/project/tags',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `compile_shell` varchar(200) NOT NULL COMMENT '编译脚本',
  `stop_shell` varchar(200) NOT NULL DEFAULT '' COMMENT '停止服务脚本',
  `restart_shell` varchar(200) NOT NULL COMMENT '重启服务脚本',
  `project_id` int(11) NOT NULL COMMENT '项目id',
  `need_audit` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否需要审核，0不需要，1需要',
  `svn_account` varchar(100) NOT NULL DEFAULT '' COMMENT 'svn 账号',
  `svn_password` varchar(100) NOT NULL DEFAULT '' COMMENT 'svn 密码',
  `jvm_args` varchar(255) NOT NULL DEFAULT '' COMMENT 'JVM参数表',
  PRIMARY KEY (`module_id`),
  KEY `idx_moduleName` (`module_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='模块详情';

-- ----------------------------
--  Table structure for `t_role`
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
  `role_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `role_name` varchar(40) NOT NULL COMMENT '角色名称',
  `role_type` int(11) NOT NULL DEFAULT '0' COMMENT '角色类型',
  `remarks` varchar(200) NOT NULL COMMENT '备注',
  `operator` bigint(20) NOT NULL DEFAULT '0' COMMENT '操作者',
  `last_modify` datetime NOT NULL COMMENT '操作时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色定义';

-- ----------------------------
--  Table structure for `t_role_app_relation`
-- ----------------------------
DROP TABLE IF EXISTS `t_role_app_relation`;
CREATE TABLE `t_role_app_relation` (
  `role_id` int(11) NOT NULL COMMENT '角色id',
  `app_id` int(11) NOT NULL COMMENT '应用系统id',
  PRIMARY KEY (`role_id`,`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='记录角色对应用系统的访问关系';

-- ----------------------------
--  Table structure for `t_role_menu_relation`
-- ----------------------------
DROP TABLE IF EXISTS `t_role_menu_relation`;
CREATE TABLE `t_role_menu_relation` (
  `role_id` int(11) NOT NULL COMMENT '角色id',
  `menu_id` int(11) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色与菜单对应关系';

-- ----------------------------
--  Table structure for `t_role_res_relation`
-- ----------------------------
DROP TABLE IF EXISTS `t_role_res_relation`;
CREATE TABLE `t_role_res_relation` (
  `role_id` int(11) NOT NULL COMMENT '角色id',
  `res_id` int(11) NOT NULL COMMENT '资源ID',
  PRIMARY KEY (`role_id`,`res_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色与资源对应关系';

-- ----------------------------
--  Table structure for `t_server`
-- ----------------------------
DROP TABLE IF EXISTS `t_server`;
CREATE TABLE `t_server` (
  `server_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `server_name` varchar(100) NOT NULL COMMENT '主键',
  `ip` varchar(40) NOT NULL COMMENT 'ip地址',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `group_id` int(11) NOT NULL COMMENT '所在的服务器组',
  PRIMARY KEY (`server_id`),
  KEY `idx_groupId` (`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='服务器表';

-- ----------------------------
--  Table structure for `t_server_deploy_history`
-- ----------------------------
DROP TABLE IF EXISTS `t_server_deploy_history`;
CREATE TABLE `t_server_deploy_history` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `history_id` int(11) NOT NULL COMMENT '发布历史id',
  `server_id` int(11) NOT NULL COMMENT '服务器id',
  `server_name` varchar(100) NOT NULL COMMENT '服务器名称',
  `server_ip` varchar(40) NOT NULL COMMENT '服务器IP',
  `startup_time` datetime DEFAULT NULL COMMENT '该服务器发布时间',
  `deploy_status` tinyint(4) NOT NULL COMMENT ' 1-发布成功，2-发布失败 3-等待部署',
  PRIMARY KEY (`id`),
  KEY `idx_history_id` (`history_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='发布历史';

-- ----------------------------
--  Table structure for `t_server_deploy_log`
-- ----------------------------
DROP TABLE IF EXISTS `t_server_deploy_log`;
CREATE TABLE `t_server_deploy_log` (
  `log_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `server_deploy_id` int(11) NOT NULL COMMENT '服务器部署记录的id，对应的是t_server_deploy_history的主键',
  `shell_log` varchar(1000) NOT NULL DEFAULT '' COMMENT 'shell 日志',
  `create_time` datetime NOT NULL COMMENT '插入时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_server_deploy_id_uindex` (`server_deploy_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='服务器发布shell日志记录';

-- ----------------------------
--  Table structure for `t_server_group`
-- ----------------------------
DROP TABLE IF EXISTS `t_server_group`;
CREATE TABLE `t_server_group` (
  `group_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '组ID',
  `group_name` varchar(50) NOT NULL DEFAULT '' COMMENT '组名',
  `env_id` int(11) NOT NULL COMMENT '所属环境',
  `module_id` int(11) NOT NULL COMMENT '关联的模块',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='服务器组';

-- ----------------------------
--  Table structure for `t_stat_all`
-- ----------------------------
DROP TABLE IF EXISTS `t_stat_all`;
CREATE TABLE `t_stat_all` (
  `stat_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `env_id` int(11) NOT NULL COMMENT '环境ID',
  `deploy_times` int(11) NOT NULL COMMENT '发布次数=success+failure',
  `success` int(11) NOT NULL COMMENT '成功次数',
  `failure` int(11) NOT NULL COMMENT '失败次数',
  PRIMARY KEY (`stat_id`),
  UNIQUE KEY `uniq_key_stat_date_env` (`stat_date`,`env_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='总的发布次数，根据deploy_time统计，未开始发布和result/stop的不算';

-- ----------------------------
--  Table structure for `t_stat_project`
-- ----------------------------
DROP TABLE IF EXISTS `t_stat_project`;
CREATE TABLE `t_stat_project` (
  `stat_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `env_id` int(11) NOT NULL COMMENT '环境ID',
  `project_id` int(11) NOT NULL COMMENT '项目ID',
  `deploy_times` int(11) NOT NULL COMMENT '发布次数=success+failure',
  `success` int(11) NOT NULL COMMENT '成功次数',
  `failure` int(11) NOT NULL COMMENT '失败次数',
  PRIMARY KEY (`stat_id`),
  UNIQUE KEY `uniq_key_stat_date_env_project` (`stat_date`,`env_id`,`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目的发布次数统计，根据deploy_time统计，未开始发布和result/stop的不算';

-- ----------------------------
--  Table structure for `t_url_resource`
-- ----------------------------
DROP TABLE IF EXISTS `t_url_resource`;
CREATE TABLE `t_url_resource` (
  `res_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `parent_res_id` int(11) NOT NULL DEFAULT '0' COMMENT '父资源ID',
  `url_name` varchar(30) NOT NULL COMMENT '名称',
  `uri` varchar(200) NOT NULL COMMENT '资源',
  `operator` bigint(20) NOT NULL DEFAULT '0' COMMENT '操作者',
  `last_modify` datetime NOT NULL COMMENT '更新时间',
  `sequence` int(11) NOT NULL DEFAULT '0' COMMENT '排序id号',
  `app_id` int(11) NOT NULL COMMENT '应用系统id',
  `url_type` smallint(6) NOT NULL DEFAULT '1' COMMENT '资源类型，如1 url,2 button 等',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`res_id`),
  UNIQUE KEY `uniq_Key_app_uri` (`app_id`,`uri`),
  KEY `idx_Key_parent_id` (`parent_res_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2131415868 DEFAULT CHARSET=utf8 COMMENT='权限控制的资源';

-- ----------------------------
--  Records of `t_url_resource`
-- ----------------------------
BEGIN;
INSERT INTO `t_url_resource` VALUES ('29453733', '0', '冻结/解冻管理员', '/admin/account/lockOrUnlockAccount.do', '1', '2018-03-27 15:01:40', '0', '1', '1', '2018-03-27 15:01:40'), ('101325062', '0', '加载初始化的项目和服务器组数据', '/admin/project/moduleBaseInfo', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('102820637', '0', '回滚', '/admin/deploy/rollBack.do', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('225384434', '0', '创建上线单主页', '/admin/deploy/create.xhtml', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('244718962', '0', '获取项目', '/admin/project/getProject', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('286952581', '0', '读取角色菜单', '/admin/account/allAppMenus', '1', '2018-03-27 15:01:40', '0', '1', '1', '2018-03-27 15:01:40'), ('405161938', '0', '重启服务', '/admin/project/restartServer.do', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('418198772', '0', '查询各项目的发布情况', '/admin/stat/queryStatProject', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('537609575', '0', '增加/修改角色信息', '/admin/account/updateRole.do', '1', '2018-03-27 15:01:40', '0', '1', '1', '2018-03-27 15:01:40'), ('540530756', '0', '发布详情主页', '/admin/deploy/view.xhtml', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('561626920', '0', '按环境统计主页', '/admin/stat/statAll.xhtml', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('578506625', '0', '环境列表', '/admin/env/listEnv.xhtml', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('590675340', '0', '低质量发布主页', '/admin/stat/lowQualityRank.xhtml', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('614414224', '0', '发布记录列表', '/admin/deploy/list', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('615557575', '0', '增加/修改角色主页', '/admin/account/editRole.xhtml', '1', '2018-03-27 15:01:40', '0', '1', '1', '2018-03-27 15:01:40'), ('616097641', '0', '读取所有角色列表', '/admin/account/allRoles', '1', '2018-03-27 15:01:40', '0', '1', '1', '2018-03-27 15:01:40'), ('723930349', '0', '审核发布记录', '/admin/deploy/audit.do', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('740189465', '0', '读取管理员信息', '/admin/account/getAdmin', '1', '2018-03-27 15:01:40', '0', '1', '1', '2018-03-27 15:01:40'), ('744239864', '0', '保存项目', '/admin/project/save.do', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('816462416', '0', '读取管理员列表', '/admin/account/queryAccount', '1', '2018-03-27 15:01:40', '0', '1', '1', '2018-03-27 15:01:40'), ('833599740', '0', '查询分支列表', '/admin/deploy/listRepository', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('931031267', '0', '拒绝发布记录', '/admin/deploy/reject.do', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('972136918', '0', '编辑模块', '/admin/project/editModule.xhtml', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1050797901', '0', '项目详情', '/admin/project/projectDetail', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1056731337', '0', '增加/修改管理员信息', '/admin/account/updateAccount.do', '1', '2018-03-27 15:01:40', '0', '1', '1', '2018-03-27 15:01:40'), ('1095019264', '0', '查询低质量发布数据', '/admin/stat/queryLowQualityRank', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1114507269', '0', 'REDO发布记录', '/admin/deploy/redo.do', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1199879837', '0', '查询所有项目的发布情况', '/admin/stat/queryStatAll', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1264419240', '0', '角色列表', '/admin/account/queryRole', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1298862981', '0', '管理员列表主页', '/admin/account/listAccount.xhtml', '1', '2018-03-27 15:01:40', '0', '1', '1', '2018-03-27 15:01:40'), ('1392474487', '0', '发布记录列表主页', '/admin/deploy/list.xhtml', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1495227657', '0', '按项目统计主页', '/admin/stat/statProject.xhtml', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1508724792', '0', '发布主页', '/admin/deploy/deploy.xhtml', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1534282863', '0', '开始发布', '/admin/deploy/startDeploy.do', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1542090117', '0', '保存环境', '/admin/env/save.do', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1549507868', '0', '项目列表', '/admin/project/listProject.xhtml', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1556062695', '0', '查询模块服务器列表', '/admin/deploy/queryModuleServer', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1558663308', '0', '获取模块数据', '/admin/project/getModule', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1574603878', '0', '角色列表主页', '/admin/account/listRole.xhtml', '1', '2018-03-27 15:01:40', '0', '1', '1', '2018-03-27 15:01:40'), ('1609851906', '0', '取消发布记录', '/admin/deploy/cancel.do', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1634833334', '0', '创建上线单', '/admin/deploy/create.do', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1705677228', '0', '停止服务', '/admin/project/stopServer.do', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1710546407', '0', '发布详情', '/admin/deploy/getDeployHistory', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1746249879', '0', '读取服务器发布日志', '/admin/deploy/getServerDeployLog', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1787261272', '0', '删除模块', '/admin/project/deleteModule.do', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1854899268', '0', '增加/修改管理员主页', '/admin/account/editAccount.xhtml', '1', '2018-03-27 15:01:40', '0', '1', '1', '2018-03-27 15:01:40'), ('1875555807', '0', '查看项目', '/admin/project/viewProject.xhtml', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('1942032933', '0', 'ping服务器', '/admin/project/ping', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('2012793559', '0', '保存模块详情', '/admin/project/saveModule.do', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('2080419363', '0', '读取项目列表', '/admin/project/list', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('2093356949', '0', '正在进行发布的数量', '/admin/deploy/getDeployingNum', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41'), ('2131415867', '0', '读取环境列表', '/admin/env/list', '1', '2018-03-27 15:01:41', '0', '1', '1', '2018-03-27 15:01:41');
insert into `t_app_define` ( `app_name`, `display_name`, `app_key`, `summary`, `app_status`, `login_url`, `sequence`, `operator`, `last_modify`, `bug_report_url`, `create_time`) values ( 'deployment-system', '版本发布系统', '9KlB5Cc5QEwS9/P5f1R2pw==', '版本发布系统', '1', 'http://deploy.ibeiliao.test.net/', '100000', '0', '2017-03-02 11:48:03', '', '2017-03-02 11:48:03');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
