create table topicos(
    id bigint not null auto_increment,
    titulo varchar(200) not null,
    mensaje text not null,
    fecha_creacion datetime not null default CURRENT_TIMESTAMP,
    estado enum('NO_RESPONDIDO', 'NO_SOLUCIONADO', 'SOLUCIONADO', 'CERRADO') not null default 'NO_RESPONDIDO',
    autor_id bigint not null,
    curso_id bigint not null,
    primary key(id),

    constraint fk_topico_autor_id foreign key (autor_id) references usuarios(id) on DELETE CASCADE,
    constraint fk_topico_curso_id foreign key (curso_id) references cursos(id) on DELETE CASCADE

);