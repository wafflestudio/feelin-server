create table block
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

alter table block
    add unique index idx_block_tui_fui (to_user_id, from_user_id);
