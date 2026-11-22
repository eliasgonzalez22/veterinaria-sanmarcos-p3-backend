package cl.duoc.sanmarcos.usuarios.dto;

public record LoginResponse(
        String token,
        String tipo,
        long expiraEnSegundos,
        UsuarioResponse usuario
) {
}
