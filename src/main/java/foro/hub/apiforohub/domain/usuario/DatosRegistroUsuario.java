package foro.hub.apiforohub.domain.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DatosRegistroUsuario(
        @NotBlank(message = "El nombre es requerido, no puede ser null")
        String nombre,
        @NotBlank @Email(message = "El email debe ser una dirección de correo electrónico con formato valido y no puede ser null")
        String correo_electronico,
        @NotBlank(message = "La contraseña es requerida, no puede ser null")
        String contrasenia
) {
}
