create table artist
(
    id          CHAR(36) not null,
    artist_name varchar(255),
    primary key (id)
);

create table album
(
    id           CHAR(36) not null,
    cover        varchar(255),
    description  varchar(255),
    release_date date     not null,
    title        varchar(255),
    artist_id    CHAR(36),
    foreign key (artist_id) references artist (id),
    primary key (id)
);

create table featuring_artist
(
    id        CHAR(36) not null,
    artist_id CHAR(36),
    track_id  CHAR(36),
    foreign key (artist_id) references artist (id),
    primary key (id)
);

create table user
(
    id                CHAR(36) not null,
    created_at        datetime(6),
    updated_at        datetime(6),
    birth_date        date,
    country_code      varchar(255),
    email             varchar(255),
    introduction      varchar(500),
    name              varchar(255),
    password          varchar(255),
    phone_number      varchar(255),
    profile_image_url varchar(255),
    role              varchar(255),
    user_id           CHAR(36) not null,
    username          varchar(255),
    primary key (id)
);

create table follow
(
    id           CHAR(36) not null,
    created_at   datetime(6),
    updated_at   datetime(6),
    from_user_id CHAR(36),
    to_user_id   CHAR(36),
    foreign key (from_user_id) references user (id),
    foreign key (to_user_id) references user (id),
    primary key (id)
);

create table post
(
    id          CHAR(36) not null,
    created_at  datetime(6),
    updated_at  datetime(6),
    content     varchar(1000),
    title       varchar(255),
    playlist_id CHAR(36),
    user_id     CHAR(36),
    primary key (id)
);

create table likes
(
    id         CHAR(36) not null,
    created_at datetime(6),
    updated_at datetime(6),
    post_id    CHAR(36),
    user_id    CHAR(36),
    foreign key (post_id) references post (id),
    foreign key (user_id) references user (id),
    primary key (id)
);

create table playlist
(
    id             CHAR(36)   not null,
    created_at     datetime(6),
    updated_at     datetime(6),
    playlist_id    BINARY(16) not null,
    playlist_order MEDIUMTEXT,
    thumbnail      varchar(255),
    user_id        CHAR(36),
    primary key (id)
);

create table track
(
    id       CHAR(36) not null,
    title    varchar(255),
    album_id CHAR(36),
    primary key (id)
);

create table verify_token
(
    id                  CHAR(36) not null,
    created_at          datetime(6),
    updated_at          datetime(6),
    access_token        varchar(255),
    authentication_code varchar(255),
    country_code        varchar(255),
    email               varchar(255),
    phone_number        varchar(255),
    refresh_token       varchar(255),
    is_verified         bit,
    primary key (id)
);

alter table album
    add unique index idx_album_title (title);
alter table artist
    add unique index idx_artist_an (artist_name);
alter table follow
    add unique index idx_follow_tui_fui (to_user_id, from_user_id);
alter table likes
    add unique index idx_likes_ui_pi (user_id, post_id);
alter table playlist
    add unique index idx_playlist_pi (playlist_id);
alter table track
    add unique index idx_track_title (title);
alter table user
    add unique index idx_user_pn_cc_email (phone_number, country_code, email);
alter table user
    add unique index idx_user_username (username);
alter table verify_token
    add unique index idx_verify_token_pn_cc_email (phone_number, country_code, email);
