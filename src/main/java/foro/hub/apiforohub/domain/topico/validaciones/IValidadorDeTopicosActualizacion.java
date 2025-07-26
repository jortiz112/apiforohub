package foro.hub.apiforohub.domain.topico.validaciones;

import foro.hub.apiforohub.domain.topico.DatosActualizacionTopico;

public interface IValidadorDeTopicosActualizacion {
    void validar(Long id, DatosActualizacionTopico datos);
}
