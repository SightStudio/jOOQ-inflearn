create table actor
(
    actor_id    int unsigned auto_increment
        primary key,
    first_name  varchar(45) not null,
    last_name   varchar(45) not null,
    last_update timestamp default CURRENT_TIMESTAMP
        not null on update CURRENT_TIMESTAMP
)
    charset = utf8mb3;

create index idx_actor_last_name
    on actor (last_name);

create table category
(
    category_id int unsigned auto_increment
        primary key,
    name        varchar(25)                         not null,
    last_update timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
)
    charset = utf8mb3;

create table language
(
    language_id int unsigned auto_increment
        primary key,
    name        char(20)                            not null,
    last_update timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
)
    charset = utf8mb3;

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
    charset = utf8mb3;

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
    charset = utf8mb3;

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
    charset = utf8mb3;

