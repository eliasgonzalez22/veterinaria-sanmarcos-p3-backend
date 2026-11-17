package cl.duoc.sanmarcos.usuarios.dto;

/**
 * Respuesta del login: el token JWT, su tipo, cuantos segundos dura
 * y los datos publicos del usuario (sin la contrasena).
 */
public record LoginResponse(
        String token,
        String tipo,
        long expiraEnSegundos,
        UsuarioResponse usuario
) {
}
