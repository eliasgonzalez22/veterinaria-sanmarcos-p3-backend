package cl.duoc.sanmarcos.usuarios.security;

import cl.duoc.sanmarcos.usuarios.exception.TooManyAttemptsException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginAttemptService {
    private static final int MAX_FALLOS = 5;
    private static final Duration BLOQUEO = Duration.ofMinutes(5);

    private record Estado(int fallos, Instant bloqueadoHasta) {}

    private final Map<String, Estado> intentos = new ConcurrentHashMap<>();

    public void verificar(String clave) {
        Estado e = intentos.get(clave);
        if (e != null && e.bloqueadoHasta() != null && Instant.now().isBefore(e.bloqueadoHasta())) {
            throw new TooManyAttemptsException();
        }
    }

    public void fallo(String clave) {
        intentos.merge(clave, new Estado(1, null), (actual, nuevo) -> {
            int fallos = actual.fallos() + 1;
            return fallos >= MAX_FALLOS
                    ? new Estado(0, Instant.now().plus(BLOQUEO))
                    : new Estado(fallos, null);
        });
    }

    public void exito(String clave) {
        intentos.remove(clave);
    }
}
