package foro.hub.apiforohub.domain.usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    UserDetails findByCorreoElectronico(String login);

    @Query("""
            SELECT u FROM Usuario u
            WHERE (:nombreUsuario IS NULL OR u.nombre LIKE %:nombreUsuario%)
            AND u.activo = true
            """)
    Page<Usuario> findByUsuarioNombreAll(String nombreUsuario, Pageable paginacion);
}

