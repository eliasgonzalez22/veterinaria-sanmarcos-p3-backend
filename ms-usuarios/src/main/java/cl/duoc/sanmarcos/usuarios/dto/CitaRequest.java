package cl.duoc.sanmarcos.usuarios.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record CitaRequest(
        Long duenoId,
        @NotNull(message = "El servicio es obligatorio") Long servicioId,
        @NotBlank(message = "El nombre de la mascota es obligatorio") @Size(max = 60) String mascotaNombre,
        @NotBlank(message = "La especie de la mascota es obligatoria") @Size(max = 40) String mascotaEspecie,
        @Size(max = 60) String mascotaRaza,
        @NotNull(message = "La fecha y hora son obligatorias") @Future(message = "La cita debe ser en una fecha futura") LocalDateTime fechaHora,
        @Size(max = 255) String motivo) {
}
