package cl.duoc.sanmarcos.usuarios.security;

import cl.duoc.sanmarcos.usuarios.model.RolNombre;

/**
 * Datos del usuario que viajan dentro del JWT y quedan disponibles
 * en los controladores con @AuthenticationPrincipal.
 */
public record UsuarioAutenticado(Long id, String email, String rol) {

    public boolean esAdministrador() {
        return RolNombre.ADMINISTRADOR.name().equals(rol);
    }
}
