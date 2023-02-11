alter table post
    add constraint post_ibfk_1 foreign key (playlist_id) references playlist (id);
