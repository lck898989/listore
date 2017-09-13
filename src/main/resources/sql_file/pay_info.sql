crete table listore_pay_info (
id int(11) not null auto_increment,
user_id int(11) default null,
order_no bigint(20) default null comment '订单号',
pay_platform int(10) default null comment '支付平台 1：支付宝 2：微信',
platform_number varchar(200) default null comment '支付宝支付流水号',
create_time datetime default null,
update_time datetime default null,
PRIMARY key(id)

)engine=InnoDB auto_increment=53 default charset=utf8