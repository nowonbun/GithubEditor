drop database nowonbun_blog;
create database nowonbun_blog;

use nowonbun_blog;

create table category (
	code char(4) not null,
	name nvarchar(255) not null,
	uniqcode varchar(255) unique not null,
	p_category_code char(4) null,
	isactive bit not null default 1,
	seq int null,
	primary key(code),
	foreign key (p_category_code) references category(code)
) comment = 'master';

create table post(
	idx int auto_increment not null,
	category_code char(4) not null,
	title nvarchar(1024) not null,
	contents longtext not null,
	tag nvarchar(2048) null,
	createddate datetime,
	lastupdateddate datetime,
	isdeleted bit default 0,
	isreservation bit default 0,
	
	primary key(idx),
	foreign key (category_code) references category(code)
) comment = 'transaction';

create table attachment(
	idx int auto_increment not null,
	post_idx int null,
	data longblob not null,
	type varchar(255) null,
	filename varchar(255) null,
	createddate datetime,
	lastupdateddate datetime,
	isdeleted bit default 0,
	
	primary key(idx),
	foreign key (post_idx) references post(idx)
) comment = 'transaction';

create table cronhistory(
	idx int auto_increment not null,
	processedDate datetime,
	state int,
	
	primary key(idx)
) comment = 'transaction';

create table cronpost(
	cronhistory_idx int not null,
	post_idx int not null,
	
	foreign key (cronhistory_idx) references cronhistory(idx),
	foreign key (post_idx) references post(idx)
) comment = 'map';

create table analysis(
	idx int auto_increment not null,
	url varchar(255),
	referrer varchar(255),
	browser varchar(255),
	createddate datetime,	
	primary key(idx)
) comment = 'analysis';