package foro.hub.apiforohub.controller;

import foro.hub.apiforohub.domain.curso.Curso;
import foro.hub.apiforohub.domain.topico.DatosActualizacionTopico;
import foro.hub.apiforohub.domain.topico.DatosDetalleTopico;
import foro.hub.apiforohub.domain.topico.DatosListaTopico;
import foro.hub.apiforohub.domain.topico.Topico;
import foro.hub.apiforohub.domain.usuario.*;
import foro.hub.apiforohub.domain.usuario.validaciones.IValidadorDeUsuarios;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
@SecurityRequirement(name = "bearer-key")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private List<IValidadorDeUsuarios> validadores;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @PostMapping
    public ResponseEntity<DatosDetalleUsuario> registrar(@RequestBody @Valid DatosRegistroUsuario datos, UriComponentsBuilder uriComponentsBuilder) {
        // Validaciones
        validadores.forEach(v -> v.validar(datos));

        // Encriptamos la contraseña antes de crear el usuario
        String contraseniaEncriptada = passwordEncoder.encode(datos.contrasenia());

        // Creamos un nuevo record con la contraseña encriptada
        var datosConContraseniaEncriptada = new DatosRegistroUsuario(
                datos.nombre(),
                datos.correo_electronico(),
                contraseniaEncriptada
        );

        var usuario = new Usuario(datosConContraseniaEncriptada);
        usuarioRepository.save(usuario);
        var uri = uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).body(new DatosDetalleUsuario(usuario));
    }

    @GetMapping
    public ResponseEntity<Page<DatosListaUsuario>> listar(@RequestParam(name = "nombre", required = false) String nombreUsuario,
                                                          @PageableDefault(size = 10, sort = {"nombre"}) Pageable paginacion) {
        // Si se provee el parámetro nombre se hace la consulta o si no pongo parámetros
        // se listan todos los usuarios con el mismo método, todo en uno solo
        var page = usuarioRepository.findByUsuarioNombreAll(nombreUsuario, paginacion)
                .map(DatosListaUsuario::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Transactional // Buena práctica para asegurar que la sesión de la BDD esté activa
    public ResponseEntity<DatosDetalleUsuario> detallar(@PathVariable Long id) {
        // Busca el usuario. Si no existe, orElseThrow lanza la excepción.
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));

        // Verificamos que el usuario esté activo.
        if (!usuario.getActivo()) {
            throw new EntityNotFoundException("usuario no encontrado con id: " + id);
        }

        // Si se encuentra, el código continúa y devuelve el DTO.
        var datosDetalle = new DatosDetalleUsuario(usuario);
        return ResponseEntity.ok(datosDetalle);
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<DatosDetalleUsuario> actualizar(@PathVariable Long id, @RequestBody @Valid DatosActualizacionUsuario datos) {
        // Validamos que el usaurio que se quiere actualizar exista
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("usuario no encontrado con id: " + id));

        // Verificamos que el usuario esté activo antes de actualizar.
        if (!usuario.getActivo()) {
            throw new EntityNotFoundException("No se puede actualizar un usuario eliminado con Id: " + id);
        }

        // Actualizamos la información directamente (sin validadores de duplicados)
        usuario.actualizarInformacion(datos);

        return ResponseEntity.ok(new DatosDetalleUsuario(usuario));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        // Validamos que el usuario que se quiere actualizar exista
        Optional<Usuario> usuarioAEliminar = usuarioRepository.findById(id);

        if (usuarioAEliminar.isPresent() && usuarioAEliminar.get().getActivo()) {
            Usuario usuario = usuarioAEliminar.get();
            // Eliminamos el usuario lógicamente con el campo activo en false
            usuario.eliminar();
            return ResponseEntity.noContent().build();
        } else {
            // Si no se encuentra, lanzamos una excepción
            throw new EntityNotFoundException("Usuario no encontrado con id: " + id);
        }
    }

    @Transactional
    @PutMapping("/{id}/contrasenia")
    public ResponseEntity<DatosRespuestaCambioContrasenia> cambioContrasenia(@PathVariable Long id, @RequestBody @Valid DatosCambioContraseniaUsuario datos) {
        // Validamos que el usaurio que se quiere cambiar la contrasenia exista
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("usuario no encontrado con id: " + id));

        // Verificamos que el usuario esté activo antes de cambiar la contrasenia.
        if (!usuario.getActivo()) {
            throw new EntityNotFoundException("No se puede cambiar de contraseña a un usuario eliminado con Id: " + id);
        }

        // Encriptamos la nueva contraseña con BCrypt
        String contraseniaEncriptada = passwordEncoder.encode(datos.contrasenia());

        // Actualizamos la contraseña
        usuario.actualizarContrasenia(contraseniaEncriptada);

        // Creamos la respuesta exitosa
        var respuesta = new DatosRespuestaCambioContrasenia(
                "Contraseña cambiada exitosamente y encriptada con BCrypt en la base de datos"
        );

        return ResponseEntity.ok(respuesta);
    }


}
