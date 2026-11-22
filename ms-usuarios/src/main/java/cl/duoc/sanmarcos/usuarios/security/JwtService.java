package cl.duoc.sanmarcos.usuarios.security;

import cl.duoc.sanmarcos.usuarios.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey clave;
    private final long expiracionMs;

    public JwtService(@Value("${jwt.secret:}") String secreto,
                      @Value("${jwt.expiracion-minutos:60}") long expiracionMinutos) {
        if (secreto == null || secreto.isBlank()) {
            log.warn("JWT_SECRET no definido: se genera una clave aleatoria (los tokens dejan de valer al reiniciar).");
            this.clave = Jwts.SIG.HS256.key().build();
        } else {
            byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
            if (bytes.length < 32) {
                throw new IllegalStateException("JWT_SECRET debe tener al menos 32 caracteres");
            }
            this.clave = Keys.hmacShaKeyFor(bytes);
        }
        this.expiracionMs = expiracionMinutos * 60_000L;
    }

    public String generarToken(Usuario usuario) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("id", usuario.getId())
                .claim("rol", usuario.getRol().getNombre())
                .claim("nombre", usuario.getNombre())
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMs))
                .signWith(clave)
                .compact();
    }

    public UsuarioAutenticado validar(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        Long id = ((Number) claims.get("id")).longValue();
        return new UsuarioAutenticado(id, claims.getSubject(), claims.get("rol", String.class));
    }

    public long getExpiracionSegundos() {
        return expiracionMs / 1000;
    }
}
