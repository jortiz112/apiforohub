package foro.hub.apiforohub.domain.usuario.validaciones;

import foro.hub.apiforohub.domain.ValidacionException;
import foro.hub.apiforohub.domain.usuario.DatosRegistroUsuario;
import foro.hub.apiforohub.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorRegistroUsuariosDuplicados implements IValidadorDeUsuarios {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void validar(DatosRegistroUsuario datos) {
        var usuarioExistente = usuarioRepository.findByCorreoElectronico(datos.correo_electronico());

        if (usuarioExistente != null) {
            throw new ValidacionException("Ya existe un usuario registrado con el correo electrónico: " + datos.correo_electronico());
        }
    }
}