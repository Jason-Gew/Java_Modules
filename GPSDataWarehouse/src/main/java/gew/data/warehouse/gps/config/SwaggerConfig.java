package gew.data.warehouse.gps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Jason/GeW
 * @since 2019-03-08
 */
@Profile({"dev", "test"})
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("gew.data.warehouse.gps.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("GPS Data Warehouse")
                .description("GPS Data Warehouse Service By Jason-GeW")
                .termsOfServiceUrl("https://github.com/Jason-Gew")
                .version("1.0.0")
                .contact(new Contact("Jason Wu","https://github.com/Jason-Gew",
                        "jason.ge.wu@gmail.com"))
                .build();
    }
}
