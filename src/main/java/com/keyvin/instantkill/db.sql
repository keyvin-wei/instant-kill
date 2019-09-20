
CREATE TABLE `tb_user` (
`id`  int NOT NULL AUTO_INCREMENT ,
`user_id`  bigint NOT NULL COMMENT '用户ID，手机号' ,
`nickname`  varchar(255) NOT NULL COMMENT '名称' ,
`password`  varchar(255) NULL COMMENT 'MD5（MD5（pass明文+固定salt）+salt）' ,
`salt`  varchar(255) NULL COMMENT '加密salt' ,
`head`  varchar(255) NULL COMMENT '头像路径' ,
`register_date`  datetime NULL COMMENT '注册时间' ,
`last_login_date`  datetime NULL COMMENT '上次登录时间' ,
`login_count`  int NULL DEFAULT '0' COMMENT '登录次数' ,
PRIMARY KEY (`id`)
);



