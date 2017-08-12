create table listore_product (
id int(11) not null auto_increment comment '商品id',
category_id int(11) not null comment '分类ID，对应listore_category表的主键',
name varchar(100) not null comment '商品名称',
subtitle varchar(200) default null comment '商品副标题',
main_image varchar(500) default null comment '商品主图,url相对地址',
sub_images text comment '图片地址，json格式，扩展用',
detail text comment '商品详情',
price decimal(20,2) not null comment '价格，单位-元，保留两位小数',
stock int(11) not null comment '库存数量',
status int(6) default '1' comment '商品状态，1-在售，2-下架，3-删除',
create_time datetime default null,
update_time datetime default null,
primary key(id))
engine=InnoDB auto_increment = 26 default charset=utf8;