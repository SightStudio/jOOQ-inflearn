create table actor
(
    actor_id    int unsigned auto_increment
        primary key,
    first_name  varchar(45)                         not null,
    last_name   varchar(45)                         not null,
    last_update timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
)
    charset = utf8;

create index idx_actor_last_name
    on actor (last_name);

create table category
(
    category_id int unsigned auto_increment primary key,
    name        varchar(25)                         not null,
    last_update timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
)
    charset = utf8;

create table country
(
    country_id  int unsigned auto_increment
        primary key,
    country     varchar(50)                         not null,
    last_update timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
)
    charset = utf8;

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
    charset = utf8;

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
    charset = utf8;

create index idx_fk_city_id
    on address (city_id);

create index idx_fk_country_id
    on city (country_id);

create table film_text
(
    film_id     int          not null
        primary key,
    title       varchar(255) not null,
    description text         null
)
    engine = MyISAM
    charset = utf8;

create fulltext index idx_title_description
    on film_text (title, description);

create table language
(
    language_id int unsigned auto_increment
        primary key,
    name        char(20)                            not null,
    last_update timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
)
    charset = utf8;

create table film
(
    film_id              int unsigned auto_increment
        primary key,
    title                varchar(255)                                                            not null,
    description          text                                                                    null,
    release_year         year                                                                    null,
    language_id          int unsigned                                                            not null,
    original_language_id int unsigned                                                            null,
    rental_duration      tinyint unsigned                        default '3'                     not null,
    rental_rate          decimal(4, 2)                           default 4.99                    not null,
    length               smallint unsigned                                                       null,
    replacement_cost     decimal(5, 2)                           default 19.99                   not null,
    rating               enum ('G', 'PG', 'PG-13', 'R', 'NC-17') default 'G'                     null,
    special_features     set ('Trailers', 'Commentaries', 'Deleted Scenes', 'Behind the Scenes') null,
    last_update          timestamp                               default CURRENT_TIMESTAMP       not null on update CURRENT_TIMESTAMP,
    constraint fk_film_language
        foreign key (language_id) references language (language_id)
            on update cascade,
    constraint fk_film_language_original
        foreign key (original_language_id) references language (language_id)
            on update cascade
)
    charset = utf8;

create index idx_fk_language_id
    on film (language_id);

create index idx_fk_original_language_id
    on film (original_language_id);

create index idx_title
    on film (title);

create table film_actor
(
    actor_id    int unsigned                        not null,
    film_id     int unsigned                        not null,
    last_update timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    primary key (actor_id, film_id),
    constraint fk_film_actor_actor
        foreign key (actor_id) references actor (actor_id)
            on update cascade,
    constraint fk_film_actor_film
        foreign key (film_id) references film (film_id)
            on update cascade
)
    charset = utf8;

create index idx_fk_film_id
    on film_actor (film_id);

create table film_category
(
    film_id     int unsigned                        not null,
    category_id int unsigned                        not null,
    last_update timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    primary key (film_id, category_id),
    constraint fk_film_category_category
        foreign key (category_id) references category (category_id)
            on update cascade,
    constraint fk_film_category_film
        foreign key (film_id) references film (film_id)
            on update cascade
)
    charset = utf8;

create table staff
(
    staff_id    int unsigned auto_increment
        primary key,
    first_name  varchar(45)                          not null,
    last_name   varchar(45)                          not null,
    address_id  int unsigned                         not null,
    picture     mediumblob                           null,
    email       varchar(50)                          null,
    store_id    int unsigned                         not null,
    active      tinyint(1) default 1                 not null,
    username    varchar(16)                          not null,
    password    varchar(40) collate utf8_bin         null,
    last_update timestamp  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint fk_staff_address
        foreign key (address_id) references address (address_id)
            on update cascade
)
    charset = utf8;

create index idx_fk_address_id
    on staff (address_id);

create index idx_fk_store_id
    on staff (store_id);

create table store
(
    store_id         int unsigned auto_increment
        primary key,
    manager_staff_id int unsigned                        not null,
    address_id       int unsigned                        not null,
    last_update      timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint idx_unique_manager
        unique (manager_staff_id),
    constraint fk_store_address
        foreign key (address_id) references address (address_id)
            on update cascade,
    constraint fk_store_staff
        foreign key (manager_staff_id) references staff (staff_id)
            on update cascade
)
    charset = utf8;

create table customer
(
    customer_id int unsigned auto_increment
        primary key,
    store_id    int unsigned                         not null,
    first_name  varchar(45)                          not null,
    last_name   varchar(45)                          not null,
    email       varchar(50)                          null,
    address_id  int unsigned                         not null,
    active      tinyint(1) default 1                 not null,
    create_date datetime                             not null,
    last_update timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint fk_customer_address
        foreign key (address_id) references address (address_id)
            on update cascade,
    constraint fk_customer_store
        foreign key (store_id) references store (store_id)
            on update cascade
)
    charset = utf8;

create index idx_fk_address_id
    on customer (address_id);

create index idx_fk_store_id
    on customer (store_id);

create index idx_last_name
    on customer (last_name);

create table inventory
(
    inventory_id int unsigned auto_increment
        primary key,
    film_id      int unsigned                        not null,
    store_id     int unsigned                        not null,
    last_update  timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint fk_inventory_film
        foreign key (film_id) references film (film_id)
            on update cascade,
    constraint fk_inventory_store
        foreign key (store_id) references store (store_id)
            on update cascade
)
    charset = utf8;

create index idx_fk_film_id
    on inventory (film_id);

create index idx_store_id_film_id
    on inventory (store_id, film_id);

create table rental
(
    rental_id    int auto_increment
        primary key,
    rental_date  datetime                            not null,
    inventory_id int unsigned                        not null,
    customer_id  int unsigned                        not null,
    return_date  datetime                            null,
    staff_id     int unsigned                        not null,
    last_update  timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint rental_date
        unique (rental_date, inventory_id, customer_id),
    constraint fk_rental_customer
        foreign key (customer_id) references customer (customer_id)
            on update cascade,
    constraint fk_rental_inventory
        foreign key (inventory_id) references inventory (inventory_id)
            on update cascade,
    constraint fk_rental_staff
        foreign key (staff_id) references staff (staff_id)
            on update cascade
)
    charset = utf8;

create table payment
(
    payment_id   int unsigned auto_increment
        primary key,
    customer_id  int unsigned                        not null,
    staff_id     int unsigned                        not null,
    rental_id    int                                 null,
    amount       decimal(5, 2)                       not null,
    payment_date datetime                            not null,
    last_update  timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint fk_payment_customer
        foreign key (customer_id) references customer (customer_id)
            on update cascade,
    constraint fk_payment_rental
        foreign key (rental_id) references rental (rental_id)
            on update cascade on delete set null,
    constraint fk_payment_staff
        foreign key (staff_id) references staff (staff_id)
            on update cascade
)
    charset = utf8;

create index idx_fk_customer_id
    on payment (customer_id);

create index idx_fk_staff_id
    on payment (staff_id);

create index idx_fk_customer_id
    on rental (customer_id);

create index idx_fk_inventory_id
    on rental (inventory_id);

create index idx_fk_staff_id
    on rental (staff_id);

alter table staff
    add constraint fk_staff_store
        foreign key (store_id) references store (store_id)
            on update cascade;

create index idx_fk_address_id
    on store (address_id);

