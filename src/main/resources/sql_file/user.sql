create table listore_user (
 id int(11) not null,
 username varchar(50) not null,
 password varchar(50) not null,
 email varchar(50) default null,
 phone varchar(50) default null,
 question varchar(100) default null,
 answer varchar(100) default null,
 role int(4) not null,
 create_time datetime not null,
 update_time datetime not null,
 primary key (id),
 unique key user_name_umique(username) using btree)
 engine = InnoDB auto_increment = 21
 default charset = utf-8;