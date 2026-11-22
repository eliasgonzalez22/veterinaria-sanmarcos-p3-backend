package cl.duoc.sanmarcos.usuarios.dto;

import cl.duoc.sanmarcos.usuarios.model.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        String rol,
        boolean activo,
        LocalDateTime fechaCreacion
) {
    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getNombre(),
                u.getEmail(),
                u.getRol().getNombre(),
                u.isActivo(),
                u.getFechaCreacion()
        );
    }
}
