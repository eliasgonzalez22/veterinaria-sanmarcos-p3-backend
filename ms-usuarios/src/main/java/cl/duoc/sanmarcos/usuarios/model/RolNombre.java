package cl.duoc.sanmarcos.usuarios.model;

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
