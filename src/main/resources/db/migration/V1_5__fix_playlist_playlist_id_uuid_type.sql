CREATE FUNCTION BIN_TO_UUID(bin BINARY(16))
    RETURNS CHAR(36) DETERMINISTIC
BEGIN
    DECLARE hex CHAR(32);
    SET hex = HEX(bin);
    RETURN LOWER(CONCAT(LEFT(hex, 8), '-', MID(hex, 9, 4), '-', MID(hex, 13, 4), '-', MID(hex, 17, 4), '-', RIGHT(hex, 12)));
END;

alter table playlist add column playlist_id_tmp CHAR(36);
update playlist set playlist_id_tmp = BIN_TO_UUID(playlist_id);
alter table playlist drop column playlist_id;
alter table playlist rename column playlist_id_tmp TO playlist_id;
