package foro.hub.apiforohub.domain.topico;

public record DatosActualizacionTopico(
        String titulo,
        String mensaje,
        Estado estado,
        String curso
) {
}
