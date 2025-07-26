alter table topicos add column activo tinyint(1) not null default 1;
update topicos set activo = 1 where activo is null;