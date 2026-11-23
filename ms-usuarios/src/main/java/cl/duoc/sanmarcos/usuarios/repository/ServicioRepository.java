package cl.duoc.sanmarcos.usuarios.repository;

import cl.duoc.sanmarcos.usuarios.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Long id);
    List<Servicio> findByActivoTrueOrderByCategoriaAscNombreAsc();
}
