package foro.hub.apiforohub.domain.topico;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface TopicoRepository extends JpaRepository<Topico, Long> {
    //Para evitar duplicados se verifica su existencia en la BDD
    Optional<Topico> findByTituloAndMensaje(String titulo, String mensaje);

    // Para la nueva búsqueda por curso y año, sí se pone un valor en cualquiera
    // de los parámetros y el otro se deja en null, o se pone un valor en los dos
    // parámetros, o no se pone ningún parámetro para listar todos los tópicos
    //para cualquiera de estas opciones sirve la misma consulta, en insomnia se
    //separa cada una de estas opciones para verificar su funcionamiento
    @Query("""
    SELECT t
    FROM Topico t
    WHERE (:nombreCurso IS NULL OR t.curso.nombre LIKE %:nombreCurso%)
    AND (:anio IS NULL OR YEAR(t.fechaCreacion) = :anio)
    AND t.activo = true
    """)
    Page<Topico> findByCursoNombreAndAnioOrAll(@Param("nombreCurso") String nombreCurso,
                                          @Param("anio") Integer anio, Pageable paginacion);
}

