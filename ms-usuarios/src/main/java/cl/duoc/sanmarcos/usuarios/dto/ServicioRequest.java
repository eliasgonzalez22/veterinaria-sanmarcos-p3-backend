package cl.duoc.sanmarcos.usuarios.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ServicioRequest(
        @NotBlank @Size(max = 20) String codigo,
        @NotBlank @Size(max = 40) String categoria,
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 40) String especie,
        @Min(value = 5, message = "La duracion minima es 5 minutos") int duracion,
        @Min(value = 0, message = "El precio no puede ser negativo") int precio,
        @Size(max = 255) String obs) {
}
