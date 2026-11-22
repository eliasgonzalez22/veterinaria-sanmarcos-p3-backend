package cl.duoc.sanmarcos.usuarios.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    public static final String ESQUEMA_JWT = "bearerAuth";

    @Bean
    public OpenAPI apiUsuarios() {
        return new OpenAPI()
                .info(new Info()
                        .title("Veterinaria San Marcos - Microservicio de Usuarios")
                        .version("v1")
                        .description("Registro, inicio de sesion con JWT, roles y CRUD de usuarios. "
                                + "Para probar rutas protegidas: haz login, copia el token y pegalo en Authorize."))
                .components(new Components().addSecuritySchemes(ESQUEMA_JWT,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
