package cl.duoc.sanmarcos.usuarios.model;

/**
 * Nombres de los roles del caso Veterinaria San Marcos.
 * Se usan en el codigo para no escribir los textos a mano.
 */
public enum RolNombre {
    ADMINISTRADOR("Gestiona el sistema completo, usuarios y roles"),
    RECEPCIONISTA("Personal de la clinica: ve, confirma y reagenda citas"),
    DUENO("Dueno de mascota: solicita citas y ve solo lo suyo");

    private final String descripcion;

    RolNombre(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
