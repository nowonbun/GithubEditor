drop database nowonbun_blog;
create database nowonbun_blog;

use nowonbun_blog;

create table category (
	code char(4) not null,
	name nvarchar(255) not null,
	uniqcode varchar(255) not null,
	isactive bit not null default 1,
	primary key(code)
) comment = 'master';

create table post(
	idx int auto_increment not null,
	category_code char(4) not null,
	title nvarchar(1024) not null,
	contents longtext not null,
	createddate datetime,
	lastupdateddate datetime,
	isdeleted bit default 0,
	
	primary key(idx),
	foreign key (category_code) references category(code)
) comment = 'transaction';

create table attachment(
	idx int auto_increment not null,
	data longblob not null,
	createddate datetime,
	lastupdateddate datetime,
	isdeleted bit default 0,
	
	primary key(idx)
) comment = 'transaction';

create table post_attachemt(
	post_idx int not null,
	attach_idx int not null,
	
	foreign key (post_idx) references post(idx),
	foreign key (attach_idx) references attachment(idx)
) comment = 'map';