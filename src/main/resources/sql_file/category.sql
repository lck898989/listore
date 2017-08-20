create table listore_category (
id int(11) not null auto_increment,
parent_id int(11) default null,
name varchar(50) default null,
status tinyint(1) default '1' comment '1表示正常，0表示已经废弃',
sort_order int(4) default null,
create_time datetime default null,
update_time datetime default null,
primary key(id))
engine=InnoDB auto_increment=100032 default charset=utf8;