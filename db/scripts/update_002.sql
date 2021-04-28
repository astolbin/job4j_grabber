create table if not exists post (
    id serial primary key not null,
    name varchar(200) not null,
    text varchar(2000),
    link varchar(200) not null unique,
    created timestamp
);