alter table users add column if not exists nickname varchar(255);
alter table users add column if not exists avatar_url varchar(1024);

update users
set nickname = coalesce(nullif(nickname, ''), full_name)
where nickname is null or nickname = '';
