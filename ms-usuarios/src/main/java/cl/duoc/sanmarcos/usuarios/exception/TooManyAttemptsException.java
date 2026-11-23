package cl.duoc.sanmarcos.usuarios.exception;

public class TooManyAttemptsException extends RuntimeException {
    public TooManyAttemptsException() {
        super("Demasiados intentos fallidos. Intenta nuevamente en unos minutos.");
    }
}
