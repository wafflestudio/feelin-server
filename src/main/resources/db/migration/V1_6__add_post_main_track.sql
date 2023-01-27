create table post_main_track
(
    post_id               CHAR(36) not null,
    title                 varchar(255),
    artist                varchar(255),
    thumbnail             varchar(255),
    post_main_track_order integer
);

alter table post_main_track
    add constraint idx_post_main_track_order foreign key (post_id) references post (id) on delete cascade;


