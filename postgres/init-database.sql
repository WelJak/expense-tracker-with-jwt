create table et_users(
user_id varchar(255) primary key not null,
username varchar(255) not null,
first_name varchar(30) not null,
last_name varchar(30) not null,
email varchar(60) not null,
password varchar(255) not null,
role varchar(255) not null
);

create table et_categories(
category_id varchar(255) primary key not null,
user_id varchar(255) not null,
title varchar(30) not null,
description varchar(255) not null
);

alter table et_categories add constraint category_users_fk foreign key (user_id) references et_users(user_id);

create table et_transactions(
transaction_id varchar(255) primary key not null,
category_id varchar(255) not null,
user_id varchar(255) not null,
amount numeric(10,2) not null,
note varchar(255) not null,
transaction_date timestamp not null
);

alter table et_transactions add constraint transaction_category_fk foreign key (category_id) references et_categories(category_id);

alter table et_transactions add constraint transactions_users_fk foreign key (user_id) references et_users(user_id);

insert into et_users values ('test', 'testusername', 'firstname', 'lastname', 'testemail@gmail.com','$2y$12$n94.t/4OMO5Xtuh/ULTElu097Ycrl9uWP7tTkyRPJErqMcbn.EkGm', 'ROLE_ADMIN');
insert into et_users values ('test1', 'testusername1', 'firstname1', 'lastname1', 'testemail1@gmail.com','$2y$12$l/aWj8gXzP0zA7uKxeAS3Og2UrDiB2cAjY1/BMaO9IKcfcnvuo6ay', 'ROLE_USER');
