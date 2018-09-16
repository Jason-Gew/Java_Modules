package gew.caching.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("gew.caching.controller"))
                .paths(PathSelectors.any())
                .build();
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot Caching with Redis")
                .description("Spring Caching By Using Redis with MySQL JPA;\nProvide direct Redis CRUD and Advanced Object Caching.")
                .termsOfServiceUrl("https://github.com/Jason-Gew/Java_Modules/blob/master/LICENSE")
                .contact(new Contact("Jason/GeW", "https://github.com/Jason-Gew", "jason.ge.wu@gmail.com"))
                .version("0.0.1")
                .license("Apache License 2.0")
                .build();
    }
}