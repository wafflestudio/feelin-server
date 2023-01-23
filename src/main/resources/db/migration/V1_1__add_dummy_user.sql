insert into user (id, created_at, updated_at, birth_date, country_code, email, introduction, name, password,
                  phone_number, profile_image_url, role, user_id,
                  username) value ('fbfc94ca-755e-4eb4-b4e9-36820a9b44ca', now(), now(), '2000-01-01', '',
                                   'admin@feelin.com', 'Hello World!', 'John Doe',
                                   '$2a$12$j9ykWp4H6hHQiojtMqG1vOr9SSwUThke0FigQ4p3Gbfs8eAbxmLve', '', null, 'user',
                                   '4b07590d-11f0-449e-be4b-58b529a2f152', 'admin');
insert into user (id, created_at, updated_at, birth_date, country_code, email, introduction, name, password,
                  phone_number, profile_image_url, role, user_id,
                  username) value ('9f62fc9d-7c42-4684-bdfa-03ee754e1c76', now(), now(), '2000-01-01', '',
                                   'userA@feelin.com', 'Hello World!', 'John Doe',
                                   '$2a$12$vzyebY9I0ttBTg9Zb2G8.OPxBzhO5YVCAYsezCvMP79Q.y8VBCwSm', '', null, 'user',
                                   '8ba26ca7-0c4b-4702-893f-90a387140326', 'userA');
insert into user (id, created_at, updated_at, birth_date, country_code, email, introduction, name, password,
                  phone_number, profile_image_url, role, user_id,
                  username) value ('ed924aef-1e51-4c8b-87f7-fe714ce9d39f', now(), now(), '2000-01-01', '',
                                   'userB@feelin.com', 'Hello World!', 'John Doe',
                                   '$2a$12$9MBcLqxYU6zWFNq/1p1gauP7uoSse5YXXGaqrt7fWrLHLP0mrZmny', '', null, 'user',
                                   '012bc4b8-e2c9-4728-a3cd-09d1234c63f0', 'userB');
insert into verify_token (id, created_at, updated_at, access_token, authentication_code, country_code, email,
                          phone_number, refresh_token,
                          is_verified) value ('7b30b781-e85e-4795-a4c8-f1b832f42960', now(), now(), '', '123456', '',
                                              'userA@feelin.com', '', null, true);
insert into verify_token (id, created_at, updated_at, access_token, authentication_code, country_code, email,
                          phone_number, refresh_token,
                          is_verified) value ('57400f89-a2d1-4133-ad07-877f30bb7884', now(), now(), '', '123456', '',
                                              'userB@feelin.com', '', null, true);
insert into follow (id, created_at, updated_at, from_user_id, to_user_id) value ('edaf670b-a7a4-4bfe-bd6c-1feda02eb72c',
                                                                                 now(), now(),
                                                                                 '9f62fc9d-7c42-4684-bdfa-03ee754e1c76',
                                                                                 'fbfc94ca-755e-4eb4-b4e9-36820a9b44ca');
insert into follow (id, created_at, updated_at, from_user_id, to_user_id) value ('b650f9d3-8b78-4a83-87da-b126125862bb',
                                                                                 now(), now(),
                                                                                 'ed924aef-1e51-4c8b-87f7-fe714ce9d39f',
                                                                                 'fbfc94ca-755e-4eb4-b4e9-36820a9b44ca');
