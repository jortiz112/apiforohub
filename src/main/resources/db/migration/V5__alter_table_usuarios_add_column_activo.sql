alter table usuarios add column activo tinyint(1) not null default 1;
update usuarios set activo = 1 where activo is null;