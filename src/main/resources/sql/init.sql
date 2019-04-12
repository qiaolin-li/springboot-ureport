/** 建表 sql */
create table ureport_file_tbl (
 id_ int primary key auto_increment,
  name_ varchar(100) not null,
  content_ mediumblob,
  create_time_ timestamp default now(),
  update_time_ timestamp default now()
)


