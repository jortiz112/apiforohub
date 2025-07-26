create table respuestas (
    id bigint not null auto_increment,
    mensaje text not null,
    topico_id bigint not null,
    fecha_creacion datetime not null default CURRENT_TIMESTAMP,
    autor_id bigint not null,
    solucion boolean not null default false,
    primary key(id),

    constraint fk_respuesta_topico_id foreign key(topico_id) references topicos(id) ON DELETE CASCADE,
    constraint fk_respuesta_autor_id foreign key (autor_id) references usuarios(id) ON DELETE CASCADE
);