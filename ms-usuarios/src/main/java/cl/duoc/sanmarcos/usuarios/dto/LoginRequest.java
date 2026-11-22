package cl.duoc.sanmarcos.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es valido")
        @Schema(example = "admin@sanmarcos.cl")
        String email,

        @NotBlank(message = "La contrasena es obligatoria")
        @Schema(example = "123456")
        String clave
) {
}
