package cl.duoc.sanmarcos.usuarios.controller;

import cl.duoc.sanmarcos.usuarios.dto.LoginRequest;
import cl.duoc.sanmarcos.usuarios.dto.LoginResponse;
import cl.duoc.sanmarcos.usuarios.dto.RegistroRequest;
import cl.duoc.sanmarcos.usuarios.dto.UsuarioResponse;
import cl.duoc.sanmarcos.usuarios.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rutas publicas (no requieren token).
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticacion", description = "Registro e inicio de sesion (publicas)")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar un dueno de mascota",
            description = "Crea el usuario con rol DUENO (lo asigna el servidor). La contrasena se guarda con hash BCrypt.")
    @ApiResponse(responseCode = "201", description = "Usuario creado")
    @ApiResponse(responseCode = "400", description = "Datos invalidos")
    @ApiResponse(responseCode = "409", description = "El correo ya esta registrado")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(req));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion",
            description = "Busca el usuario en la tabla, compara el hash de la contrasena y entrega un JWT con vigencia.")
    @ApiResponse(responseCode = "200", description = "Login correcto, devuelve el token")
    @ApiResponse(responseCode = "400", description = "Datos invalidos")
    @ApiResponse(responseCode = "401", description = "Correo o contrasena incorrectos (mensaje generico)")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
}
