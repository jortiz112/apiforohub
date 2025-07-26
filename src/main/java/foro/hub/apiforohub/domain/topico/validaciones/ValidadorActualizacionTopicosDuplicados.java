package foro.hub.apiforohub.domain.topico.validaciones;

import foro.hub.apiforohub.domain.topico.DatosActualizacionTopico;
import foro.hub.apiforohub.domain.topico.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//La registramos como un componente de Spring para poder inyectarla en el controlador
@Component
public class ValidadorActualizacionTopicosDuplicados implements IValidadorDeTopicosActualizacion {

    //Inyectamos el TopicoRepository que necesitamos para hacer la consulta
    @Autowired
    private TopicoRepository topicoRepository;

    // Este método ya cumple con el contrato de la interfaz
    @Override
    public void validar(Long idTopicoAActualizar, DatosActualizacionTopico datos) {
        // Si el título o el mensaje no se están actualizando, no hay nada que validar.
        if (datos.titulo() == null || datos.mensaje() == null) {
            return;
        }

        // Buscamos si ya existe un tópico con el nuevo título y mensaje
        var topicoDuplicado = topicoRepository.findByTituloAndMensaje(datos.titulo(), datos.mensaje());

        // Si se encuentra un duplicado, verificamos que no sea el mismo que estamos actualizando
        if (topicoDuplicado.isPresent() && !topicoDuplicado.get().getId().equals(idTopicoAActualizar)) {
            // Si existe y tiene un ID diferente, es un duplicado real. Lanzamos una excepción.
            throw new IllegalStateException("Ya existe un tópico con el mismo título y mensaje.");
        }
    }
}
