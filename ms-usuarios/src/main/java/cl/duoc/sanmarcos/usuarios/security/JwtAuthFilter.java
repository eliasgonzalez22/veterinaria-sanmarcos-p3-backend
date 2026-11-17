package cl.duoc.sanmarcos.usuarios.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Se ejecuta en CADA peticion antes de llegar al controlador.
 * 1. Lee el encabezado "Authorization: Bearer <token>".
 * 2. Verifica firma y vencimiento con JwtService.
 * 3. Si es valido, deja al usuario autenticado con su rol (ROLE_ADMINISTRADOR, etc.).
 * Si falta o es invalido, no autentica: la ruta protegida respondera 401.
 *
 * No se marca como @Component para que Spring no lo registre dos veces;
 * se crea dentro de SecurityConfig.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String ATRIBUTO_ERROR = "jwt_error";

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String encabezado = request.getHeader("Authorization");
        if (encabezado != null && encabezado.startsWith("Bearer ")) {
            String token = encabezado.substring(7);
            try {
                UsuarioAutenticado usuario = jwtService.validar(token);
                var autoridades = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.rol()));
                var autenticacion = new UsernamePasswordAuthenticationToken(usuario, null, autoridades);
                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            } catch (ExpiredJwtException e) {
                request.setAttribute(ATRIBUTO_ERROR, "El token vencio, inicia sesion nuevamente");
            } catch (JwtException | IllegalArgumentException e) {
                request.setAttribute(ATRIBUTO_ERROR, "Token invalido o alterado");
            }
        }
        chain.doFilter(request, response);
    }
}
