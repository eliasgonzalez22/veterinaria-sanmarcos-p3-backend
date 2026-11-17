package cl.duoc.sanmarcos.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Modificacion de usuario. Los campos en null no se cambian.
 * rol y activo solo los puede cambiar el Administrador (se revisa en el servicio).
 */
public record UsuarioActualizarRequest(
        @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
        String nombre,

        @Email(message = "El correo no es valido")
        @Size(max = 120, message = "El correo no puede superar 120 caracteres")
        String email,

        @Size(min = 6, max = 72, message = "La contrasena debe tener entre 6 y 72 caracteres")
        String clave,

        String rol,

        Boolean activo
) {
}
