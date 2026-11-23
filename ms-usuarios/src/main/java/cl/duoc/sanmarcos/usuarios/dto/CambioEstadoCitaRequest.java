package cl.duoc.sanmarcos.usuarios.dto;

import cl.duoc.sanmarcos.usuarios.model.EstadoCita;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CambioEstadoCitaRequest(
        @NotNull(message = "El nuevo estado es obligatorio") EstadoCita estado,
        @Size(max = 255) String observacionClinica) {
}
