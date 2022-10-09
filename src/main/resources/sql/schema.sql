create table if not exists person (
  id varchar(60) default RANDOM_UUID() primary key,
  name varchar(30) not null,
  lastName varchar(50) not null
);