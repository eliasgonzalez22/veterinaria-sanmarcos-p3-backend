package cl.duoc.sanmarcos.usuarios.controller;

import cl.duoc.sanmarcos.usuarios.dto.CambioEstadoCitaRequest;
import cl.duoc.sanmarcos.usuarios.dto.CitaRequest;
import cl.duoc.sanmarcos.usuarios.dto.CitaResponse;
import cl.duoc.sanmarcos.usuarios.service.CitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/citas")
@Tag(name = "Citas", description = "Flujo principal de agendamiento y atencion de citas veterinarias")
@SecurityRequirement(name = "bearerAuth")
public class CitaController {
    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    @Operation(summary = "Listar citas (DUENO ve solo las suyas; VETERINARIO y ADMINISTRADOR ven todas)")
    @ApiResponse(responseCode = "200", description = "Listado de citas")
    @ApiResponse(responseCode = "401", description = "Sin token o token invalido")
    public List<CitaResponse> listar(Authentication auth) {
        return citaService.listar(auth);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar detalle de una cita")
    @ApiResponse(responseCode = "200", description = "Detalle de la cita")
    @ApiResponse(responseCode = "401", description = "Sin token o token invalido")
    @ApiResponse(responseCode = "403", description = "Sin permiso para ver esta cita")
    @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    public CitaResponse obtener(@PathVariable Long id, Authentication auth) {
        return citaService.obtener(id, auth);
    }

    @PostMapping
    @Operation(summary = "Agendar nueva cita")
    @ApiResponse(responseCode = "201", description = "Cita creada en estado PENDIENTE")
    @ApiResponse(responseCode = "400", description = "Datos invalidos o fuera de horario (09:00 a 19:00)")
    @ApiResponse(responseCode = "401", description = "Sin token o token invalido")
    @ApiResponse(responseCode = "404", description = "Servicio o dueno no encontrado")
    @ApiResponse(responseCode = "409", description = "Horario ya ocupado")
    public ResponseEntity<CitaResponse> crear(@Valid @RequestBody CitaRequest req, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.crear(req, auth));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de una cita (VETERINARIO/ADMIN: cualquiera; DUENO: solo CANCELADA)")
    @ApiResponse(responseCode = "200", description = "Estado actualizado")
    @ApiResponse(responseCode = "400", description = "Transicion de estado invalida")
    @ApiResponse(responseCode = "401", description = "Sin token o token invalido")
    @ApiResponse(responseCode = "403", description = "Rol sin permiso para esa accion")
    @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    public CitaResponse cambiarEstado(@PathVariable Long id,
                                      @Valid @RequestBody CambioEstadoCitaRequest req,
                                      Authentication auth) {
        return citaService.cambiarEstado(id, req, auth);
    }
}
