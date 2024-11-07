insert into t_application_user(id, c_username, c_password)
values ('6148b272-9cda-11ef-91f5-db035b6f4aea', 'user1', '{noop}password1'),
       ('624c5de0-9cda-11ef-a5c6-2365a3385bfc', 'user2', '{noop}password2');

insert into t_task(id, c_details, c_completed, id_application_user)
values ('7cbd4a86-9c5d-11ef-a96a-13456a082682', 'first',
        false, '6148b272-9cda-11ef-91f5-db035b6f4aea'),
       ('84263cc4-9c5d-11ef-bc48-c7ecbc94d7a6', 'second',
        true, '6148b272-9cda-11ef-91f5-db035b6f4aea'),
       ('20988e96-9cf3-11ef-8717-13d1f59c6e52', 'third',
        false, '624c5de0-9cda-11ef-a5c6-2365a3385bfc');

