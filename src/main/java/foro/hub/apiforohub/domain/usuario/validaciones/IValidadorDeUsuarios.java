package foro.hub.apiforohub.domain.usuario.validaciones;

import foro.hub.apiforohub.domain.usuario.DatosRegistroUsuario;

public interface IValidadorDeUsuarios {
    void validar(DatosRegistroUsuario datos);
}
