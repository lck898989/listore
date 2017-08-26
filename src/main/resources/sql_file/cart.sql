create table listore_cart (
id int(11) not null auto_increment,
user_id int(11) not null,
product_id int(11) default null comment '商品id',
quantity int(11) default null comment '数量',
checked int(11) default null comment '是否选择，1=已经勾选，0=未勾选',
create_time datetime default null comment '创建时间',
update_time datetime default null,
primary key(id),
key 'user_id_index' ('user_id') using btree) 
engine=InnoDB auto_increment=219 default charset=utf8;
#key 'user_id_index' 提高提取效率