CREATE TABLE usuarios (
    id bigint not null auto_increment,
    nombre varchar(100) not null,
    correo_electronico varchar(150) not null unique,
    contrasenia varchar(255) not null,

    primary key(id)
);