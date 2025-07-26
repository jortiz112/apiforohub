package foro.hub.apiforohub.controller;

import foro.hub.apiforohub.domain.curso.*;
import foro.hub.apiforohub.domain.curso.validaciones.IValidadorDeCursos;
import foro.hub.apiforohub.domain.usuario.DatosListaUsuario;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/cursos")
@SecurityRequirement(name = "bearer-key")
public class CursoController {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private List<IValidadorDeCursos> validadores;


    @Transactional
    @PostMapping
    public ResponseEntity<DatosDetalleCurso> registrar(@RequestBody @Valid DatosRegistroCurso datos, UriComponentsBuilder uriComponentsBuilder) {
        // Validaciones
        validadores.forEach(v -> v.validar(datos));

        var curso = new Curso(datos);
        cursoRepository.save(curso);
        var uri = uriComponentsBuilder.path("/cursos/{id}").buildAndExpand(curso.getId()).toUri();
        return ResponseEntity.created(uri).body(new DatosDetalleCurso(curso));
    }

    @GetMapping
    public ResponseEntity<Page<DatosListaCurso>> listar(@RequestParam(name = "nombre", required = false) String nombreCurso,
                                                        @PageableDefault(size = 10, sort = {"nombre"}) Pageable paginacion) {
        // Si se provee el parámetro nombre se hace la consulta o si no pongo parámetros
        // se listan todos los cursos con el mismo método, todo en uno solo
        var page = cursoRepository.findByCursoNombreAll(nombreCurso, paginacion)
                .map(DatosListaCurso::new);
        return ResponseEntity.ok(page);
    }
}
