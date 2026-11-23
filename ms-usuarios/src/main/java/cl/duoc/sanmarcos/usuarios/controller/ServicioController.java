package cl.duoc.sanmarcos.usuarios.controller;

import cl.duoc.sanmarcos.usuarios.dto.ServicioRequest;
import cl.duoc.sanmarcos.usuarios.dto.ServicioResponse;
import cl.duoc.sanmarcos.usuarios.service.ServicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/servicios")
@Tag(name = "Servicios", description = "Catalogo de servicios de la veterinaria (requiere JWT)")
@SecurityRequirement(name = "bearerAuth")
public class ServicioController {
    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @GetMapping
    @Operation(summary = "Listar servicios activos (cualquier usuario con sesion)")
    @ApiResponse(responseCode = "200", description = "Lista de servicios")
    @ApiResponse(responseCode = "401", description = "Sin token o token invalido")
    public List<ServicioResponse> listar() {
        return servicioService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver un servicio (cualquier usuario con sesion)")
    @ApiResponse(responseCode = "200", description = "Servicio encontrado")
    @ApiResponse(responseCode = "401", description = "Sin token o token invalido")
    @ApiResponse(responseCode = "404", description = "No existe")
    public ServicioResponse obtener(@PathVariable Long id) {
        return servicioService.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crear servicio (solo ADMINISTRADOR)")
    @ApiResponse(responseCode = "201", description = "Servicio creado")
    @ApiResponse(responseCode = "400", description = "Datos invalidos")
    @ApiResponse(responseCode = "401", description = "Sin token o token invalido")
    @ApiResponse(responseCode = "403", description = "Rol insuficiente")
    @ApiResponse(responseCode = "409", description = "El codigo ya existe")
    public ResponseEntity<ServicioResponse> crear(@Valid @RequestBody ServicioRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioService.crear(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar servicio (solo ADMINISTRADOR)")
    @ApiResponse(responseCode = "200", description = "Servicio actualizado")
    @ApiResponse(responseCode = "400", description = "Datos invalidos")
    @ApiResponse(responseCode = "401", description = "Sin token o token invalido")
    @ApiResponse(responseCode = "403", description = "Rol insuficiente")
    @ApiResponse(responseCode = "404", description = "No existe")
    @ApiResponse(responseCode = "409", description = "El codigo ya existe")
    public ServicioResponse actualizar(@PathVariable Long id, @Valid @RequestBody ServicioRequest req) {
        return servicioService.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar servicio (solo ADMINISTRADOR)")
    @ApiResponse(responseCode = "204", description = "Servicio desactivado")
    @ApiResponse(responseCode = "401", description = "Sin token o token invalido")
    @ApiResponse(responseCode = "403", description = "Rol insuficiente")
    @ApiResponse(responseCode = "404", description = "No existe")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        servicioService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
