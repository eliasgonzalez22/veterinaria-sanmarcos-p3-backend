package cl.duoc.sanmarcos.usuarios.controller;

import cl.duoc.sanmarcos.usuarios.dto.LoginRequest;
import cl.duoc.sanmarcos.usuarios.dto.LoginResponse;
import cl.duoc.sanmarcos.usuarios.dto.RegistroRequest;
import cl.duoc.sanmarcos.usuarios.dto.UsuarioResponse;
import cl.duoc.sanmarcos.usuarios.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticacion", description = "Registro, inicio de sesion y sesion actual")
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
            description = "Busca el usuario en la tabla, compara el hash de la contrasena y entrega un JWT con vigencia. "
                    + "Tras 5 intentos fallidos seguidos (mismo correo e IP) bloquea por 5 minutos.")
    @ApiResponse(responseCode = "200", description = "Login correcto, devuelve el token")
    @ApiResponse(responseCode = "400", description = "Datos invalidos")
    @ApiResponse(responseCode = "401", description = "Correo o contrasena incorrectos (mensaje generico)")
    @ApiResponse(responseCode = "429", description = "Demasiados intentos fallidos")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req, HttpServletRequest http) {
        return ResponseEntity.ok(authService.login(req, http.getRemoteAddr()));
    }

    @GetMapping("/me")
    @Operation(summary = "Datos de la sesion actual",
            description = "Requiere JWT valido (con iss y aud correctos). Devuelve el usuario dueno del token.")
    @ApiResponse(responseCode = "200", description = "Usuario de la sesion")
    @ApiResponse(responseCode = "401", description = "Sin token, token invalido o cuenta inactiva")
    public ResponseEntity<UsuarioResponse> me(@RequestHeader("Authorization") String authorization) {
        String token = authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;
        return ResponseEntity.ok(authService.me(token));
    }
}
