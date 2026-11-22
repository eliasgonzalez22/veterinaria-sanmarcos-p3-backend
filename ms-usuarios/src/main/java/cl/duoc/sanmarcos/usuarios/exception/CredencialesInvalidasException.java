package cl.duoc.sanmarcos.usuarios.exception;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() {
        super("Correo o contrasena incorrectos");
    }
}
