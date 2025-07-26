package foro.hub.apiforohub.domain.curso.validaciones;

import foro.hub.apiforohub.domain.ValidacionException;
import foro.hub.apiforohub.domain.curso.CursoRepository;
import foro.hub.apiforohub.domain.curso.DatosRegistroCurso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorRegistroCursosDuplicados implements IValidadorDeCursos {

    @Autowired
    private CursoRepository cursoRepository;


    @Override
    public void validar(DatosRegistroCurso datos) {
        var cursoExistente = cursoRepository.findByNombre(datos.nombre());

        if (cursoExistente.isPresent()) {
            throw new ValidacionException("Ya existe un curso registrado con el nombre: " + datos.nombre());
        }
    }
}
