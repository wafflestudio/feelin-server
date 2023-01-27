alter table playlist add column playlist_id_tmp CHAR(36);

update playlist
set playlist_id_tmp = LOWER(CONCAT(LEFT(HEX(playlist_id), 8), '-', MID(HEX(playlist_id), 9, 4), '-',
                                   MID(HEX(playlist_id), 13, 4), '-', MID(HEX(playlist_id), 17, 4), '-',
                                   RIGHT(HEX(playlist_id), 12)));

alter table playlist drop column playlist_id;
alter table playlist rename column playlist_id_tmp TO playlist_id;
