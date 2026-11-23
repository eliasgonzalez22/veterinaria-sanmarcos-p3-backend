package cl.duoc.sanmarcos.usuarios.service;

import cl.duoc.sanmarcos.usuarios.dto.ServicioRequest;
import cl.duoc.sanmarcos.usuarios.dto.ServicioResponse;
import cl.duoc.sanmarcos.usuarios.exception.ConflictoException;
import cl.duoc.sanmarcos.usuarios.exception.RecursoNoEncontradoException;
import cl.duoc.sanmarcos.usuarios.model.Servicio;
import cl.duoc.sanmarcos.usuarios.repository.ServicioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServicioService {
    private final ServicioRepository repo;

    public ServicioService(ServicioRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<ServicioResponse> listar() {
        return repo.findByActivoTrueOrderByCategoriaAscNombreAsc().stream().map(ServicioResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public ServicioResponse obtener(Long id) {
        return ServicioResponse.desde(buscar(id));
    }

    @Transactional
    public ServicioResponse crear(ServicioRequest req) {
        String codigo = req.codigo().trim().toUpperCase();
        if (repo.existsByCodigo(codigo)) {
            throw new ConflictoException("Ya existe un servicio con el codigo " + codigo);
        }
        Servicio s = new Servicio();
        aplicar(s, req, codigo);
        return ServicioResponse.desde(repo.save(s));
    }

    @Transactional
    public ServicioResponse actualizar(Long id, ServicioRequest req) {
        Servicio s = buscar(id);
        String codigo = req.codigo().trim().toUpperCase();
        if (repo.existsByCodigoAndIdNot(codigo, id)) {
            throw new ConflictoException("Ya existe un servicio con el codigo " + codigo);
        }
        aplicar(s, req, codigo);
        return ServicioResponse.desde(repo.save(s));
    }

    @Transactional
    public void desactivar(Long id) {
        Servicio s = buscar(id);
        s.setActivo(false);
        repo.save(s);
    }

    private Servicio buscar(Long id) {
        return repo.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Servicio no encontrado"));
    }

    private void aplicar(Servicio s, ServicioRequest req, String codigo) {
        s.setCodigo(codigo);
        s.setCategoria(req.categoria().trim());
        s.setNombre(req.nombre().trim());
        s.setEspecie(req.especie().trim());
        s.setDuracion(req.duracion());
        s.setPrecio(req.precio());
        s.setObs(req.obs());
    }
}
