package cl.duoc.sanmarcos.usuarios.controller;

import cl.duoc.sanmarcos.usuarios.config.OpenApiConfig;
import cl.duoc.sanmarcos.usuarios.dto.UsuarioActualizarRequest;
import cl.duoc.sanmarcos.usuarios.dto.UsuarioCrearRequest;
import cl.duoc.sanmarcos.usuarios.dto.UsuarioResponse;
import cl.duoc.sanmarcos.usuarios.security.UsuarioAutenticado;
import cl.duoc.sanmarcos.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "CRUD de usuarios y roles (requiere JWT)")
@SecurityRequirement(name = OpenApiConfig.ESQUEMA_JWT)
@ApiResponse(responseCode = "401", description = "Sin token, token alterado o vencido")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/yo")
    @Operation(summary = "Datos del usuario con sesion iniciada",
            description = "Requiere JWT (cualquier rol). El usuario se obtiene del token, no de un id enviado por el cliente.")
    @ApiResponse(responseCode = "200", description = "Datos del usuario del token")
    public ResponseEntity<UsuarioResponse> yo(@AuthenticationPrincipal UsuarioAutenticado actor) {
        return ResponseEntity.ok(usuarioService.obtener(actor.id(), actor));
    }

    @GetMapping
    @Operation(summary = "Listar usuarios (solo ADMINISTRADOR)",
            description = "Requiere JWT con rol ADMINISTRADOR. Devuelve id, nombre, email, rol, activo y fechaCreacion; nunca la contrasena ni su hash.")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios")
    @ApiResponse(responseCode = "403", description = "Rol insuficiente")
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver un usuario (el mismo usuario o ADMINISTRADOR)",
            description = "Requiere JWT. Un usuario que no es ADMINISTRADOR solo puede consultar su propio id: cambiar el id en la URL responde 403.")
    @ApiResponse(responseCode = "200", description = "Datos del usuario")
    @ApiResponse(responseCode = "403", description = "Intenta ver la cuenta de otra persona")
    @ApiResponse(responseCode = "404", description = "No existe")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable Long id,
                                                   @AuthenticationPrincipal UsuarioAutenticado actor) {
        return ResponseEntity.ok(usuarioService.obtener(id, actor));
    }

    @PostMapping
    @Operation(summary = "Crear usuario con rol (solo ADMINISTRADOR)",
            description = "Requiere JWT con rol ADMINISTRADOR. Campos obligatorios: nombre (3-80), email valido y unico, clave (6-72) y rol "
                    + "(ADMINISTRADOR, RECEPCIONISTA o DUENO). La clave se guarda con hash BCrypt.")
    @ApiResponse(responseCode = "400", description = "Datos invalidos (detalle por campo en errores)")
    @ApiResponse(responseCode = "404", description = "El rol no existe")
    @ApiResponse(responseCode = "201", description = "Creado")
    @ApiResponse(responseCode = "403", description = "Rol insuficiente")
    @ApiResponse(responseCode = "409", description = "Correo ya registrado")
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioCrearRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar usuario",
            description = "Requiere JWT. Todos los campos son opcionales: solo se cambian los que vienen. El propio usuario puede cambiar "
                    + "nombre, email y clave; solo ADMINISTRADOR puede cambiar rol y activo (si no, 403). Correo repetido: 409.")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado")
    @ApiResponse(responseCode = "400", description = "Datos invalidos (detalle por campo en errores)")
    @ApiResponse(responseCode = "403", description = "Otro usuario o rol insuficiente")
    @ApiResponse(responseCode = "404", description = "No existe")
    @ApiResponse(responseCode = "409", description = "Correo ya registrado")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody UsuarioActualizarRequest req,
                                                      @AuthenticationPrincipal UsuarioAutenticado actor) {
        return ResponseEntity.ok(usuarioService.actualizar(id, req, actor));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar usuario (solo ADMINISTRADOR)",
            description = "Requiere JWT con rol ADMINISTRADOR. Borrado logico: activo = false, se conservan sus datos e historial y ya no "
                    + "puede iniciar sesion. Se reactiva con PUT {\"activo\": true}. El administrador no puede desactivarse a si mismo (409).")
    @ApiResponse(responseCode = "204", description = "Desactivado")
    @ApiResponse(responseCode = "403", description = "Rol insuficiente")
    @ApiResponse(responseCode = "404", description = "No existe")
    @ApiResponse(responseCode = "409", description = "No puedes desactivar tu propia cuenta")
    public ResponseEntity<Void> desactivar(@PathVariable Long id,
                                           @AuthenticationPrincipal UsuarioAutenticado actor) {
        usuarioService.desactivar(id, actor);
        return ResponseEntity.noContent().build();
    }
}
