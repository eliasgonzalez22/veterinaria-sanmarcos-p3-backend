package cl.duoc.sanmarcos.usuarios.dto;

import cl.duoc.sanmarcos.usuarios.model.Servicio;

public record ServicioResponse(Long id, String codigo, String categoria, String nombre,
                               String especie, int duracion, int precio, String obs, boolean activo) {
    public static ServicioResponse desde(Servicio s) {
        return new ServicioResponse(s.getId(), s.getCodigo(), s.getCategoria(), s.getNombre(),
                s.getEspecie(), s.getDuracion(), s.getPrecio(), s.getObs(), s.isActivo());
    }
}
