package cl.duoc.sanmarcos.usuarios.service;

import cl.duoc.sanmarcos.usuarios.dto.LoginRequest;
import cl.duoc.sanmarcos.usuarios.dto.LoginResponse;
import cl.duoc.sanmarcos.usuarios.dto.RegistroRequest;
import cl.duoc.sanmarcos.usuarios.dto.UsuarioResponse;
import cl.duoc.sanmarcos.usuarios.exception.CredencialesInvalidasException;
import cl.duoc.sanmarcos.usuarios.model.RolNombre;
import cl.duoc.sanmarcos.usuarios.model.Usuario;
import cl.duoc.sanmarcos.usuarios.repository.UsuarioRepository;
import cl.duoc.sanmarcos.usuarios.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, UsuarioService usuarioService,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        String email = UsuarioService.normalizarEmail(req.email());
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(CredencialesInvalidasException::new);

        if (!usuario.isActivo() || !passwordEncoder.matches(req.clave(), usuario.getClaveHash())) {
            throw new CredencialesInvalidasException();
        }

        String token = jwtService.generarToken(usuario);
        return new LoginResponse(token, "Bearer", jwtService.getExpiracionSegundos(), UsuarioResponse.desde(usuario));
    }

    @Transactional
    public UsuarioResponse registrar(RegistroRequest req) {
        Usuario nuevo = usuarioService.crearUsuario(req.nombre(), req.email(), req.clave(), RolNombre.DUENO.name());
        return UsuarioResponse.desde(nuevo);
    }
}
