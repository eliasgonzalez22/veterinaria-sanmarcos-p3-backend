package cl.duoc.sanmarcos.usuarios.exception;

/** Regla de negocio violada (por ejemplo, correo ya registrado). Se traduce a HTTP 409. */
public class ConflictoException extends RuntimeException {
    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
