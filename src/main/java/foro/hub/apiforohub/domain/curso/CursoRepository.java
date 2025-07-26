package foro.hub.apiforohub.domain.curso;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CursoRepository extends JpaRepository<Curso, Long> {
    Optional<Curso> findByNombre(String nombre);

    @Query("""
            SELECT c 
            FROM Curso c
            WHERE (:nombreCurso IS NULL OR c.nombre LIKE %:nombreCurso%)
            """)
    Page<Curso> findByCursoNombreAll(String nombreCurso, Pageable paginacion);
}
