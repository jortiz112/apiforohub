package foro.hub.apiforohub.domain.curso;

public record DatosDetalleCurso(
        Long id,
        String nombre,
        String categoria
) {
    public DatosDetalleCurso(Curso curso) {
        this(
                curso.getId(),
                curso.getNombre(),
                curso.getCategoria()
        );
    }
}
