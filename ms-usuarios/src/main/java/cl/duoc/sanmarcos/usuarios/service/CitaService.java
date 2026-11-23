package cl.duoc.sanmarcos.usuarios.service;

import cl.duoc.sanmarcos.usuarios.dto.CambioEstadoCitaRequest;
import cl.duoc.sanmarcos.usuarios.dto.CitaRequest;
import cl.duoc.sanmarcos.usuarios.dto.CitaResponse;
import cl.duoc.sanmarcos.usuarios.exception.ConflictoException;
import cl.duoc.sanmarcos.usuarios.exception.RecursoNoEncontradoException;
import cl.duoc.sanmarcos.usuarios.model.*;
import cl.duoc.sanmarcos.usuarios.repository.CitaRepository;
import cl.duoc.sanmarcos.usuarios.repository.ServicioRepository;
import cl.duoc.sanmarcos.usuarios.repository.UsuarioRepository;
import cl.duoc.sanmarcos.usuarios.security.UsuarioAutenticado;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
public class CitaService {
    private static final LocalTime HORA_APERTURA = LocalTime.of(9, 0);
    private static final LocalTime HORA_CIERRE = LocalTime.of(19, 0);

    private final CitaRepository citaRepo;
    private final UsuarioRepository usuarioRepo;
    private final ServicioRepository servicioRepo;

    public CitaService(CitaRepository citaRepo, UsuarioRepository usuarioRepo, ServicioRepository servicioRepo) {
        this.citaRepo = citaRepo;
        this.usuarioRepo = usuarioRepo;
        this.servicioRepo = servicioRepo;
    }

    @Transactional(readOnly = true)
    public List<CitaResponse> listar(Authentication auth) {
        Usuario actual = usuarioAutenticado(auth);
        List<Cita> citas = esAdminOVeterinario(actual)
                ? citaRepo.findAllByOrderByFechaHoraAsc()
                : citaRepo.findByDuenoIdOrderByFechaHoraAsc(actual.getId());
        return citas.stream().map(CitaResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public CitaResponse obtener(Long id, Authentication auth) {
        Usuario actual = usuarioAutenticado(auth);
        Cita cita = buscarCita(id);
        validarAccesoRegistro(actual, cita);
        return CitaResponse.desde(cita);
    }

    @Transactional
    public CitaResponse crear(CitaRequest req, Authentication auth) {
        Usuario actual = usuarioAutenticado(auth);

        Usuario dueno;
        if (esAdminOVeterinario(actual) && req.duenoId() != null) {
            dueno = usuarioRepo.findById(req.duenoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Dueno no encontrado"));
        } else {
            dueno = actual;
        }

        Servicio servicio = servicioRepo.findById(req.servicioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Servicio no encontrado"));
        if (!servicio.isActivo()) {
            throw new IllegalArgumentException("El servicio seleccionado no esta disponible");
        }

        LocalTime hora = req.fechaHora().toLocalTime();
        if (hora.isBefore(HORA_APERTURA) || hora.isAfter(HORA_CIERRE)) {
            throw new IllegalArgumentException("El horario de atencion es de 09:00 a 19:00 hrs");
        }

        boolean ocupado = citaRepo.existsByFechaHoraAndEstadoIn(
                req.fechaHora(), List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA));
        if (ocupado) {
            throw new ConflictoException("Ya existe una cita agendada para esa fecha y hora");
        }

        Cita cita = new Cita();
        cita.setDueno(dueno);
        cita.setServicio(servicio);
        cita.setMascotaNombre(req.mascotaNombre().trim());
        cita.setMascotaEspecie(req.mascotaEspecie().trim());
        cita.setMascotaRaza(req.mascotaRaza() != null ? req.mascotaRaza().trim() : null);
        cita.setFechaHora(req.fechaHora());
        cita.setMotivo(req.motivo() != null ? req.motivo().trim() : null);
        cita.setEstado(EstadoCita.PENDIENTE);

        return CitaResponse.desde(citaRepo.save(cita));
    }

    @Transactional
    public CitaResponse cambiarEstado(Long id, CambioEstadoCitaRequest req, Authentication auth) {
        Usuario actual = usuarioAutenticado(auth);
        Cita cita = buscarCita(id);
        validarAccesoRegistro(actual, cita);

        if (cita.getEstado() == EstadoCita.ATENDIDA || cita.getEstado() == EstadoCita.CANCELADA) {
            throw new IllegalArgumentException("No se puede modificar una cita ya " + cita.getEstado().name().toLowerCase());
        }

        if (!esAdminOVeterinario(actual) && req.estado() != EstadoCita.CANCELADA) {
            throw new AccessDeniedException("Un dueno solo puede cancelar su propia cita");
        }

        cita.setEstado(req.estado());
        if (req.observacionClinica() != null && !req.observacionClinica().isBlank()) {
            cita.setObservacionClinica(req.observacionClinica().trim());
        }

        return CitaResponse.desde(citaRepo.save(cita));
    }

    private Cita buscarCita(Long id) {
        return citaRepo.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Cita no encontrada"));
    }

    private Usuario usuarioAutenticado(Authentication auth) {
        if (auth.getPrincipal() instanceof UsuarioAutenticado ua) {
            return usuarioRepo.findById(ua.id())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Usuario autenticado no encontrado"));
        }
        String principal = auth.getName();
        if (principal.matches("\\d+")) {
            return usuarioRepo.findById(Long.parseLong(principal))
                    .orElseThrow(() -> new RecursoNoEncontradoException("Usuario autenticado no encontrado"));
        }
        return usuarioRepo.findByEmail(principal)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario autenticado no encontrado"));
    }

    private boolean esAdminOVeterinario(Usuario u) {
        String rol = u.getRol().getNombre();
        return rol.equals("ADMINISTRADOR") || rol.equals("VETERINARIO");
    }

    private void validarAccesoRegistro(Usuario actual, Cita cita) {
        if (!esAdminOVeterinario(actual) && !cita.getDueno().getId().equals(actual.getId())) {
            throw new AccessDeniedException("No tienes permiso para acceder a esta cita");
        }
    }
}
