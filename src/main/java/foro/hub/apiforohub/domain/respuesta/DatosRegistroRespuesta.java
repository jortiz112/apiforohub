package foro.hub.apiforohub.domain.respuesta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosRegistroRespuesta(
        @NotBlank(message = "El mensaje es requerido, no puede ser null")
        String mensaje,
        @NotNull(message = "El tópico es requerido, no puede ser null")
        Long topico_id,
        @NotNull(message = "El autor es requerido, no puede ser null")
        Long autor_id
) {
}
