package cl.duoc.sanmarcos.usuarios.exception;

/**
 * Login fallido. El mensaje es siempre el mismo (generico) para no revelar
 * si el correo existe o si lo que fallo fue la contrasena. Se traduce a HTTP 401.
 */
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() {
        super("Correo o contrasena incorrectos");
    }
}
