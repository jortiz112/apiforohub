package foro.hub.apiforohub.domain.respuesta;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {
    // Método para buscar respuestas duplicadas por mensaje, tópico y autor
    Optional<Respuesta> findByMensajeAndTopicoIdAndAutorId(String mensaje, Long topicoId, Long autorId);

    @Query("""
            SELECT r 
            FROM Respuesta r
            WHERE (:tituloTopico IS NULL OR r.topico.titulo LIKE %:tituloTopico%)
            AND (:nombreUsuario IS NULL OR r.autor.nombre LIKE %:nombreUsuario%)
            """)
    Page<Respuesta> findByTituloTopicoAndNombreUsuarioOrAll(String tituloTopico, String nombreUsuario, Pageable paginacion);
}
