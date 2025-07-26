package foro.hub.apiforohub.controller;

import foro.hub.apiforohub.domain.ValidacionException;
import foro.hub.apiforohub.domain.curso.Curso;
import foro.hub.apiforohub.domain.curso.CursoRepository;
import foro.hub.apiforohub.domain.topico.*;
import foro.hub.apiforohub.domain.topico.validaciones.IValidadorDeTopicos;
import foro.hub.apiforohub.domain.topico.validaciones.IValidadorDeTopicosActualizacion;
import foro.hub.apiforohub.domain.usuario.Usuario;
import foro.hub.apiforohub.domain.usuario.UsuarioRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/topicos")
@SecurityRequirement(name = "bearer-key")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private List<IValidadorDeTopicos> validadores;

    // Inyectamos la nueva lista de validadores para la actualización
    @Autowired
    private List<IValidadorDeTopicosActualizacion> validadoresActualizacion;

    @Transactional
    @PostMapping
    public ResponseEntity<DatosDetalleTopico> registrar(@RequestBody @Valid DatosRegistroTopico datos, UriComponentsBuilder uriComponentsBuilder) {
        // Validaciones
        validadores.forEach(v -> v.validar(datos));

        // Buscamos y validamos que el autor exista, si no, lanza la excepción
        Usuario usuario = usuarioRepository.findById(datos.autor_id())
                .orElseThrow(() -> new ValidacionException("No se encontró un autor con el Id proporcionado."));

        // Buscamos y validamos que el curso exista, si no, lanza la excepción
        Curso curso = cursoRepository.findByNombre(datos.curso())
                .orElseThrow(() -> new ValidacionException("No se encontró un curso con el nombre proporcionado: " + datos.curso()));

        // Creamos el nuevo tópico con el constructor que se creo en la clase Topico
        Topico topico = new Topico(datos, usuario, curso);

        // Guardamos el tópico en la base de datos
        topicoRepository.save(topico);

        var uri = uriComponentsBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new DatosDetalleTopico(topico));
    }

    @GetMapping
    public ResponseEntity<Page<DatosListaTopico>> listar(@RequestParam(name = "curso", required = false) String nombreCurso,
            @RequestParam(name = "anio", required = false) Integer anio,
            @PageableDefault(size = 10, sort = {"fechaCreacion"}) Pageable paginacion) {
        // Si se proveen AMBOS parámetros se hace la consulta o solo busca por curso o por año,
        // cualquiera de los dos, si no pongo parámetros se listan todos los tópicos con el
        // mismo método, todo en uno solo
            var page = topicoRepository.findByCursoNombreAndAnioOrAll(nombreCurso, anio, paginacion)
                    .map(DatosListaTopico::new);
            return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Transactional // Buena práctica para asegurar que la sesión de la BDD esté activa
    public ResponseEntity<DatosDetalleTopico> detallar(@PathVariable Long id) {
        // Busca el tópico. Si no existe, orElseThrow lanza la excepción.
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tópico no encontrado con id: " + id));

        // Verificamos que el tópico esté activo.
        if (!topico.getActivo()) {
            throw new EntityNotFoundException("Tópico no encontrado con id: " + id);
        }

        // Si se encuentra, el código continúa y devuelve el DTO.
        var datosDetalle = new DatosDetalleTopico(topico);
        return ResponseEntity.ok(datosDetalle);
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<DatosDetalleTopico> actualizar(@PathVariable Long id, @RequestBody @Valid DatosActualizacionTopico datos) {
        // Validamos que el tópico que se quiere actualizar exista
        Topico topico = topicoRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Tópico no encontrado con id: " + id));

        // Verificamos que el tópico esté activo antes de actualizar.
        if (!topico.getActivo()) {
            throw new EntityNotFoundException("No se puede actualizar un tópico eliminado con Id: " + id);
        }

        // Se usa el validador para la regla de negocio de duplicados
        validadoresActualizacion.forEach(v -> v.validar(id, datos));

        // Lógica para buscar el objeto Curso (si se actualiza)
        Curso curso = null;
        if (datos.curso() != null && !datos.curso().isBlank()) {
            curso = cursoRepository.findByNombre(datos.curso())
                    .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado: " + datos.curso()));
        }

        // Si todas las validaciones pasan, actualizamos la información
        topico.actualizarInformacion(datos, curso);

        return  ResponseEntity.ok(new DatosDetalleTopico(topico));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        // Validamos que el tópico que se quiere actualizar exista
        Optional<Topico> topicoAEliminar = topicoRepository.findById(id);

        if (topicoAEliminar.isPresent() && topicoAEliminar.get().getActivo()) {
            Topico topico = topicoAEliminar.get();
            // Eliminamos el tópico lógicamente con el campo activo en false
            topico.eliminar();
            return ResponseEntity.noContent().build();
        } else {
            // Si no se encuentra, lanzamos una excepción
            throw new EntityNotFoundException("Tópico no encontrado con id: " + id);
        }
    }


}
