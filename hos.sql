drop database `hos`;
create DATABASE if not exists `hos` default character set 'utf8mb4';
use `hos`;
drop table if exists `hos`.`user`;
create table `hos`.`user`(
	`id` int(11) NOT NULL COMMENT '用户id' AUTO_INCREMENT,
	`username` VARCHAR(20) NOT NULL COMMENT '用户名',
	`password` VARCHAR(64) NOT NULL COMMent '密码 MD5加密',
	`create_time` datetime(0) DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`detail` VARCHAR(255) COMMENT '用户详细描述',
     PRIMARY KEY (`id`),
	 UNIQUE KEY UQ_USER_NAME(`username`)
)ENGINE=InnoDB CHARSET=utf8mb4 COMMENT = '用户信息表';

drop table if exists `hos`.`bucket`;
create table `hos`.`bucket`(
    `id` bigint MOT NULL COMMENT '命名空间id',
    `bucket_name` VARCHAR(64) NOT NULL COMMENT '命名空间名称',
    `access` VARCHAR(10) NOT NULL COMMENT '命名空间权限控制 PRIVATE 私有空间，里面所有文件需要TOKEN访问，PUBLIC啧不需要TOKEN访问 默认私有空间',
    `bucket_type` VARCHAR(10) NOT NULL COMMENT '命名空间类型 自有空间 读写授权 只读授权 预留字段 暂不实现 默认自有空间',
    `create_time` datatime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datatime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `creator` VARCHAR(20) COMMENT '空间创建人',
    PRIMARY KEY(`id`),
    UNIQUE KEY UQ_BUCKET_NAME(`bucket_name`),
    UNIQUE KEY UQ_CREATOR(`creator`)
)ENGINE=InnoDB CHARSET=utf8mb4 COMMENT = 'HOS命名空间，是一块空间的高级抽象，对空间进行权限，日志管理 一个';
