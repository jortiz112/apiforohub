package foro.hub.apiforohub.domain.usuario;

public record DatosListaUsuario(
        Long id,
        String nombre
) {
    public DatosListaUsuario(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNombre()
        );
    }
}
