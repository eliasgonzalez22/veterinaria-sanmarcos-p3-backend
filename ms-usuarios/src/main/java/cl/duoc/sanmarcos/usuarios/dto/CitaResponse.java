package cl.duoc.sanmarcos.usuarios.dto;

import cl.duoc.sanmarcos.usuarios.model.Cita;
import cl.duoc.sanmarcos.usuarios.model.EstadoCita;
import java.time.LocalDateTime;

public record CitaResponse(
        Long id,
        Long duenoId,
        String duenoNombre,
        String duenoEmail,
        Long servicioId,
        String servicioCodigo,
        String servicioNombre,
        int servicioPrecio,
        String mascotaNombre,
        String mascotaEspecie,
        String mascotaRaza,
        LocalDateTime fechaHora,
        String motivo,
        EstadoCita estado,
        String observacionClinica) {
    public static CitaResponse desde(Cita c) {
        return new CitaResponse(
                c.getId(),
                c.getDueno().getId(),
                c.getDueno().getNombre(),
                c.getDueno().getEmail(),
                c.getServicio().getId(),
                c.getServicio().getCodigo(),
                c.getServicio().getNombre(),
                c.getServicio().getPrecio(),
                c.getMascotaNombre(),
                c.getMascotaEspecie(),
                c.getMascotaRaza(),
                c.getFechaHora(),
                c.getMotivo(),
                c.getEstado(),
                c.getObservacionClinica()
        );
    }
}
