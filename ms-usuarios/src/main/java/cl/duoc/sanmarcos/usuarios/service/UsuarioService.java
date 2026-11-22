package cl.duoc.sanmarcos.usuarios.service;

import cl.duoc.sanmarcos.usuarios.dto.UsuarioActualizarRequest;
import cl.duoc.sanmarcos.usuarios.dto.UsuarioCrearRequest;
import cl.duoc.sanmarcos.usuarios.dto.UsuarioResponse;
import cl.duoc.sanmarcos.usuarios.exception.ConflictoException;
import cl.duoc.sanmarcos.usuarios.exception.RecursoNoEncontradoException;
import cl.duoc.sanmarcos.usuarios.model.Rol;
import cl.duoc.sanmarcos.usuarios.model.Usuario;
import cl.duoc.sanmarcos.usuarios.repository.RolRepository;
import cl.duoc.sanmarcos.usuarios.repository.UsuarioRepository;
import cl.duoc.sanmarcos.usuarios.security.UsuarioAutenticado;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    static String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    @Transactional
    public Usuario crearUsuario(String nombre, String email, String clave, String nombreRol) {
        String correo = normalizarEmail(email);
        if (usuarioRepository.existsByEmail(correo)) {
            throw new ConflictoException("El correo ya esta registrado");
        }
        Usuario u = new Usuario();
        u.setNombre(nombre.trim());
        u.setEmail(correo);
        u.setClaveHash(passwordEncoder.encode(clave));
        u.setRol(buscarRol(nombreRol));
        u.setActivo(true);
        return usuarioRepository.save(u);
    }

    @Transactional
    public UsuarioResponse crear(UsuarioCrearRequest req) {
        return UsuarioResponse.desde(crearUsuario(req.nombre(), req.email(), req.clave(), req.rol()));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(UsuarioResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id, UsuarioAutenticado actor) {
        verificarMismoUsuarioOAdmin(id, actor);
        return UsuarioResponse.desde(buscar(id));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioActualizarRequest req, UsuarioAutenticado actor) {
        verificarMismoUsuarioOAdmin(id, actor);
        Usuario u = buscar(id);

        if (req.nombre() != null) {
            u.setNombre(req.nombre().trim());
        }
        if (req.email() != null) {
            String correo = normalizarEmail(req.email());
            if (usuarioRepository.existsByEmailAndIdNot(correo, id)) {
                throw new ConflictoException("El correo ya esta registrado");
            }
            u.setEmail(correo);
        }
        if (req.clave() != null) {
            u.setClaveHash(passwordEncoder.encode(req.clave()));
        }
        if (req.rol() != null || req.activo() != null) {
            if (!actor.esAdministrador()) {
                throw new AccessDeniedException("Solo el Administrador puede cambiar el rol o el estado");
            }
            if (req.rol() != null) {
                u.setRol(buscarRol(req.rol()));
            }
            if (req.activo() != null) {
                u.setActivo(req.activo());
            }
        }
        return UsuarioResponse.desde(usuarioRepository.save(u));
    }

    @Transactional
    public void desactivar(Long id, UsuarioAutenticado actor) {
        if (actor.id().equals(id)) {
            throw new ConflictoException("No puedes desactivar tu propia cuenta");
        }
        Usuario u = buscar(id);
        u.setActivo(false);
        usuarioRepository.save(u);
    }

    private void verificarMismoUsuarioOAdmin(Long id, UsuarioAutenticado actor) {
        if (!actor.esAdministrador() && !actor.id().equals(id)) {
            throw new AccessDeniedException("No puedes acceder a la cuenta de otro usuario");
        }
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario con id " + id));
    }

    private Rol buscarRol(String nombreRol) {
        String nombre = nombreRol == null ? "" : nombreRol.trim().toUpperCase(Locale.ROOT);
        return rolRepository.findByNombre(nombre)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el rol " + nombreRol));
    }
}
