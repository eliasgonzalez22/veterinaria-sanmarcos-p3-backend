package cl.duoc.sanmarcos.usuarios.security;

import cl.duoc.sanmarcos.usuarios.model.RolNombre;

public record UsuarioAutenticado(Long id, String email, String rol) {
    public boolean esAdministrador() {
        return RolNombre.ADMINISTRADOR.name().equals(rol);
    }
}
