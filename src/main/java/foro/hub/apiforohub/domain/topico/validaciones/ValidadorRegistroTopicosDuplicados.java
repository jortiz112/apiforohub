package foro.hub.apiforohub.domain.topico.validaciones;

import foro.hub.apiforohub.domain.ValidacionException;
import foro.hub.apiforohub.domain.topico.DatosRegistroTopico;
import foro.hub.apiforohub.domain.topico.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//La registramos como un componente de Spring para poder inyectarla en el controlador
@Component
public class ValidadorRegistroTopicosDuplicados implements IValidadorDeTopicos {

    @Autowired
    private TopicoRepository topicoRepository;

    // Este método ya cumple con el contrato de la interfaz
    @Override
    public void validar(DatosRegistroTopico datos) {
        var topicoDuplicado = topicoRepository.findByTituloAndMensaje(datos.titulo(), datos.mensaje());
        if (topicoDuplicado.isPresent()) {
            throw new ValidacionException("Ya existe un tópico con el mismo título y mensaje.");
        }
    }
}
