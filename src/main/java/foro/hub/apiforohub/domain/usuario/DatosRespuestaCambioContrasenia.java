package foro.hub.apiforohub.domain.usuario;

public record DatosRespuestaCambioContrasenia(
        String mensaje,
        String estado
) {
    public DatosRespuestaCambioContrasenia(String mensaje) {
        this(mensaje, "exitoso");
    }
}
