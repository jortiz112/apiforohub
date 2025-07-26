package foro.hub.apiforohub.domain.topico;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosRegistroTopico(
        @NotBlank(message = "El título es requerido, no puede ser null")
        String titulo,
        @NotBlank(message = "El mensaje es requerido, no puede ser null")
        String mensaje,
        @NotNull(message = "El autor es requerido, no puede ser null")
        Long autor_id,
        @NotBlank(message = "El curso es requerido, no puede ser null")
        String curso
) {
}
