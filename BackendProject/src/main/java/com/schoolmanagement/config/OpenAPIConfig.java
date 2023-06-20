package com.schoolmanagement.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "StudentManagement API", version = "1.0.0"),
        security =@SecurityRequirement(name = "Bearer"))
@SecurityScheme(name = "Bearer", type = SecuritySchemeType.HTTP, scheme ="Bearer")
// swagger annotationu. security katmaninda guvenlik asamamizin nasil oalcagini soylememiz lazim. Boylelikle swagger dokumantasyonu olustururken
//buna gore olusturacak.info annotationu parametreli cons gibi. baslik bilgilerini
public class OpenAPIConfig {

    // http://localhost:8080/swagger-ui/index.html#/
    //swagger endpointleri test etmek icin fazla kompleks bir yapi. Bu nedenle ilk tercihin postman olsun.
}