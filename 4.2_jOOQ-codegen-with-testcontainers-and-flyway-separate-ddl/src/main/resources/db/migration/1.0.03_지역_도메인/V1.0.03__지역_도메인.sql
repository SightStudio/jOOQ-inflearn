create table country
(
    country_id  int unsigned auto_increment
        primary key,
    country     varchar(50)                         not null,
    last_update timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
)
    charset = utf8mb3;

create table city
(
    city_id     int unsigned auto_increment
        primary key,
    city        varchar(50)                         not null,
    country_id  int unsigned                        not null,
    last_update timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint fk_city_country
        foreign key (country_id) references country (country_id)
            on update cascade
)
    charset = utf8mb3;

create table address
(
    address_id  int unsigned auto_increment
        primary key,
    address     varchar(50)                         not null,
    address2    varchar(50)                         null,
    district    varchar(20)                         not null,
    city_id     int unsigned                        not null,
    postal_code varchar(10)                         null,
    phone       varchar(20)                         not null,
    last_update timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint fk_address_city
        foreign key (city_id) references city (city_id)
            on update cascade
)
    charset = utf8mb3;

create index idx_fk_city_id
    on address (city_id);

create index idx_fk_country_id
    on city (country_id);


-- 지역도메인과 연동되는 외래키 제약조건
alter table customer
    add constraint fk_customer_address
        foreign key (address_id) references address (address_id)
            on update cascade;

alter table customer
    add constraint fk_customer_store
        foreign key (store_id) references store (store_id)
            on update cascade;

alter table staff
    add constraint fk_staff_address
        foreign key (address_id) references address (address_id)
            on update cascade;

alter table store
    add constraint fk_store_address
        foreign key (address_id) references address (address_id)
            on update cascade;

