package cl.duoc.sanmarcos.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Alta de usuario que hace el Administrador (puede elegir el rol).
 */
public record UsuarioCrearRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es valido")
        @Size(max = 120, message = "El correo no puede superar 120 caracteres")
        String email,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 6, max = 72, message = "La contrasena debe tener entre 6 y 72 caracteres")
        String clave,

        @NotBlank(message = "El rol es obligatorio (ADMINISTRADOR, RECEPCIONISTA o DUENO)")
        String rol
) {
}
