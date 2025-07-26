package foro.hub.apiforohub.domain.curso;

public record DatosListaCurso(
        long id,
        String nombre,
        String categoria
) {
    public DatosListaCurso(Curso curso) {
        this(
                curso.getId(),
                curso.getNombre(),
                curso.getCategoria()
        );
    }
}
