package cl.duoc.sanmarcos.usuarios.config;

import cl.duoc.sanmarcos.usuarios.model.Rol;
import cl.duoc.sanmarcos.usuarios.model.RolNombre;
import cl.duoc.sanmarcos.usuarios.repository.RolRepository;
import cl.duoc.sanmarcos.usuarios.repository.UsuarioRepository;
import cl.duoc.sanmarcos.usuarios.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    @Value("${app.seed.habilitado:true}")
    private boolean seedHabilitado;

    @Value("${app.seed.clave:}")
    private String claveSeed;

    public DataInitializer(RolRepository rolRepository, UsuarioRepository usuarioRepository,
                           UsuarioService usuarioService) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) {
        for (RolNombre r : RolNombre.values()) {
            if (rolRepository.findByNombre(r.name()).isEmpty()) {
                rolRepository.save(new Rol(r.name(), r.getDescripcion()));
                log.info("Rol creado: {}", r.name());
            }
        }

        if (!seedHabilitado || claveSeed == null || claveSeed.isBlank()) {
            return;
        }
        crearSiNoExiste("Administrador San Marcos", "admin@sanmarcos.cl", RolNombre.ADMINISTRADOR);
        crearSiNoExiste("Rita Recepcion", "recepcion@sanmarcos.cl", RolNombre.RECEPCIONISTA);
        crearSiNoExiste("Ana Duena", "dueno@sanmarcos.cl", RolNombre.DUENO);
    }

    private void crearSiNoExiste(String nombre, String email, RolNombre rol) {
        if (!usuarioRepository.existsByEmail(email)) {
            usuarioService.crearUsuario(nombre, email, claveSeed, rol.name());
            log.info("Usuario de prueba creado: {} ({})", email, rol.name());
        }
    }
}
