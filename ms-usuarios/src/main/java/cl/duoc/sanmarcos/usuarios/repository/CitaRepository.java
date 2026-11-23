package cl.duoc.sanmarcos.usuarios.repository;

import cl.duoc.sanmarcos.usuarios.model.Cita;
import cl.duoc.sanmarcos.usuarios.model.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findAllByOrderByFechaHoraAsc();
    List<Cita> findByDuenoIdOrderByFechaHoraAsc(Long duenoId);
    boolean existsByFechaHoraAndEstadoIn(LocalDateTime fechaHora, Collection<EstadoCita> estados);
}
