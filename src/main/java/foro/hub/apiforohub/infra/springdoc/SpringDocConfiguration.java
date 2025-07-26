package foro.hub.apiforohub.infra.springdoc;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Para que Spring pueda ver esta clase
@Configuration
//se añade esta clase para poder poner el token en la documentación generada por SpringDoc,
// generado cuando me logueo con un usuario en la página de
// http://localhost:8080/swagger-ui/index.html de la documentación
public class SpringDocConfiguration {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                //para la inclusión del token de autenticación
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme
                                                .Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                //Para incluir la descripción de la Api
                .info(new Info()
                        .title("Foro Hub API")
                        .description("API Rest de la aplicación Foro Hub, que contiene las funcionalidades CRUD de Tópicos, de cursos, de usuarios y de respuestas, cambio de contraseña encriptada con BCrypt.")
                        //Para incluir información de contacto
                        .contact(new Contact()
                                .name("Ing. Julio César Ortiz")
                                .email("jortiz112112@gmail.com"))
                        //Para incluir la licencia de uso
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://foro.hub/api/licencia")));
    }
}

