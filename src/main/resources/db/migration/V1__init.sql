create table users (
    id serial NOT NULL ,
    first_name varchar (100) NOT NULL,
    last_name varchar (100) NOT NULL,
    email varchar (200) UNIQUE ,
    password varchar (2000) NOT NULL,
    role varchar(20) NOT NULL,
    enabled boolean not null
);
