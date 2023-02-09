alter table post_main_track
    drop foreign key idx_post_main_track_order;
alter table post_main_track
    add constraint post_main_track_ibfk_1 foreign key (post_id) references post (id) on delete cascade;
