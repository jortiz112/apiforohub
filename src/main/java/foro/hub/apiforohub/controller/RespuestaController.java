package foro.hub.apiforohub.controller;

import foro.hub.apiforohub.domain.ValidacionException;
import foro.hub.apiforohub.domain.curso.Curso;
import foro.hub.apiforohub.domain.respuesta.*;
import foro.hub.apiforohub.domain.respuesta.validaciones.IValidadorDeRespuestas;
import foro.hub.apiforohub.domain.topico.*;
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

@RestController
@RequestMapping("/respuestas")
@SecurityRequirement(name = "bearer-key")
public class RespuestaController {

    @Autowired
    private RespuestaRepository respuestaRepository;

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private List<IValidadorDeRespuestas> validadores;

    @Transactional
    @PostMapping
    public ResponseEntity<DatosDetalleRespuesta> registrar(@RequestBody @Valid DatosRegistroRespuesta datos, UriComponentsBuilder uriComponentsBuilder) {
        // Validaciones
        validadores.forEach(v -> v.validar(datos));

        // Buscamos y validamos que el tópico exista, si no, lanza la excepción
        Topico topico = topicoRepository.findById(datos.topico_id())
                .orElseThrow(() -> new ValidacionException("No se encontró un tópico con el Id proporcionado."));

        // Buscamos y validamos que el autor exista, si no, lanza la excepción
        Usuario usuario = usuarioRepository.findById(datos.autor_id())
                .orElseThrow(() -> new ValidacionException("No se encontró un autor con el Id proporcionado."));


        // Creamos una nueva respuesta con el constructor que se creo en la clase Respuesta
        Respuesta respuesta = new Respuesta(datos, topico, usuario);

        // Guardamos la respuesta en la base de datos
        respuestaRepository.save(respuesta);

        var uri = uriComponentsBuilder.path("/respuestas/{id}").buildAndExpand(respuesta.getId()).toUri();
        return ResponseEntity.created(uri).body(new DatosDetalleRespuesta(respuesta));
    }

    @GetMapping
    public ResponseEntity<Page<DatosDetalleRespuesta>> listar(@RequestParam(name = "topico", required = false) String tituloTopico,
                                                            @RequestParam(name = "autor", required = false) String nombreUsuario,
                                                            @PageableDefault(size = 10, sort = {"fechaCreacion"}) Pageable paginacion) {
        // Si se proveen AMBOS parámetros se hace la consulta o solo busca por topico o por autor,
        // cualquiera de los dos, si no pongo parámetros se listan todos las respuestas con el
        // mismo método, todo en uno solo
        var page = respuestaRepository.findByTituloTopicoAndNombreUsuarioOrAll(tituloTopico, nombreUsuario, paginacion)
                .map(DatosDetalleRespuesta::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Transactional // Buena práctica para asegurar que la sesión de la BDD esté activa
    public ResponseEntity<DatosDetalleRespuesta> detallar(@PathVariable Long id) {
        // Busca la respuesta. Si no existe, orElseThrow lanza la excepción.
        Respuesta respuesta = respuestaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Respuesta no encontrada con id: " + id));

        // Si se encuentra, el código continúa y devuelve la respuesta.
        var datosDetalle = new DatosDetalleRespuesta(respuesta);
        return ResponseEntity.ok(datosDetalle);
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<DatosDetalleRespuesta> actualizar(@PathVariable Long id, @RequestBody @Valid DatosActualizacionRespuesta datos) {
        // Validamos que la respuesta que se quiere actualizar exista
        Respuesta respuesta = respuestaRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Respuesta no encontrada con id: " + id));

        // Actualizamos la información
        respuesta.actualizarInformacion(datos);

        return ResponseEntity.ok(new DatosDetalleRespuesta(respuesta));
    }
}
