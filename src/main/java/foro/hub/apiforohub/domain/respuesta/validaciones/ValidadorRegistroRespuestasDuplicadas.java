package foro.hub.apiforohub.domain.respuesta.validaciones;

import foro.hub.apiforohub.domain.ValidacionException;
import foro.hub.apiforohub.domain.respuesta.DatosRegistroRespuesta;
import foro.hub.apiforohub.domain.respuesta.RespuestaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorRegistroRespuestasDuplicadas implements IValidadorDeRespuestas {

    @Autowired
    private RespuestaRepository respuestaRepository;

    @Override
    public void validar(DatosRegistroRespuesta datos) {

        // Verificamos si ya existe una respuesta con el mismo mensaje, tópico y autor
        var respuestaDuplicada = respuestaRepository.findByMensajeAndTopicoIdAndAutorId(
                datos.mensaje(),
                datos.topico_id(),
                datos.autor_id()
        );

        if (respuestaDuplicada.isPresent()) {
            throw new ValidacionException("Ya existe una respuesta con el mismo mensaje, tópico y autor.");
        }
    }
}
