package foro.hub.apiforohub.domain.curso;

import jakarta.validation.constraints.NotBlank;

public record DatosRegistroCurso(
        @NotBlank(message = "El nombre es requerido, no puede ser null")
        String nombre,
        @NotBlank(message = "La categoría es requerida, no puede ser null")
        String categoria
) {
}
