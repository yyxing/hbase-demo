drop database `hos`;
create DATABASE if not exists `hos` default character set 'utf8mb4';
use `hos`;
drop table if exists `hos`.`user`;
create table `hos`.`user`(
	`id` int(11) NOT NULL COMMENT '用户id' AUTO_INCREMENT,
	`username` VARCHAR(20) NOT NULL COMMENT '用户名',
	`password` VARCHAR(64) NOT NULL COMMent '密码 MD5加密',
	`create_time` datetime(0) DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
	`detail` VARCHAR(255) COMMENT '用户详细描述',
   PRIMARY KEY (`id`),
	 UNIQUE KEY UQ_USER_NAME(`username`)
)ENGINE=InnoDB CHARSET=utf8mb4 COMMENT = '用户信息表';