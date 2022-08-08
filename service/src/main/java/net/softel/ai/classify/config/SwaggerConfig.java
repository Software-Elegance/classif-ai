package net.softel.ai.classify.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Value("${application.description}")
    private String appDescription = "Fancy Description";

    @Value("${application.version}")
    private String appVersion = "1.0.0";

    @Value("${organization.email}")
    private String email = "software@elegance.com";

    @Value("${organization.name}")
    private String name = "Software elegance";

    @Value("${organization.website}")
    private String website = "our Website";

    @Value("${organization.title}")
    private String title = "Classif-ai";
    @Bean
    public OpenAPI openApiInit() {
        return new OpenAPI()
                .info(new Info().title(title).version(appVersion)
                        .contact(new Contact().email(email).name(name).url(website))
                        .description(appDescription)
                        .termsOfService("http://swagger.io/terms/")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                );
    }
}
