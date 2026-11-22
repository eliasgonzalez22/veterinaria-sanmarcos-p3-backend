package cl.duoc.sanmarcos.usuarios.security;

import cl.duoc.sanmarcos.usuarios.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RespuestasSeguridad {
    private final ObjectMapper mapper;

    public RespuestasSeguridad(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public AuthenticationEntryPoint noAutenticado() {
        return (request, response, ex) -> {
            Object detalle = request.getAttribute(JwtAuthFilter.ATRIBUTO_ERROR);
            String mensaje = detalle != null ? detalle.toString() : "Debes iniciar sesion (falta el token)";
            escribir(request, response, 401, "Unauthorized", mensaje);
        };
    }

    public AccessDeniedHandler sinPermiso() {
        return (request, response, ex) ->
                escribir(request, response, 403, "Forbidden", "Tu rol no tiene permiso para esta accion");
    }

    private void escribir(HttpServletRequest req, HttpServletResponse res, int estado, String error, String mensaje)
            throws IOException {
        res.setStatus(estado);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        mapper.writeValue(res.getOutputStream(), ErrorResponse.de(estado, error, mensaje, req.getRequestURI()));
    }
}
