CREATE  TABLE listore_shipping (
id int(11) not null auto_increment,
user_id int(11) default null comment 'user id',
receiver_name varchar(20) default null comment 'name of receiver',
receiver_phone varchar(20) default null comment '收货固定电话',
receiver_mobile varchar(20) default null comment '收货移动电话',
receiver_province varchar(20) default null comment '省份',
receiver_city varchar(20) default null comment '城市',
receiver_district varchar(20) default null comment '区县',
receiver_address varchar(200) default null comment '详细地址',
receiver_zip varchar(6) default null comment '邮编',
create_time datetime default null,
update_time datetime default null,
primary key(id)
)engine=InnoDB auto_increment=30 DEFAULT charset=utf8