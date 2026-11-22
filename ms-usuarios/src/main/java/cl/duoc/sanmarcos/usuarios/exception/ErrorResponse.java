package cl.duoc.sanmarcos.usuarios.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        int estado,
        String error,
        String mensaje,
        String ruta,
        LocalDateTime fecha,
        Map<String, String> errores
) {
    public static ErrorResponse de(int estado, String error, String mensaje, String ruta) {
        return new ErrorResponse(estado, error, mensaje, ruta, LocalDateTime.now(), null);
    }
}
