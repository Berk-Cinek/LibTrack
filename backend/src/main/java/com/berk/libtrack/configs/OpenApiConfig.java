package com.berk.libtrack.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI libTrackAPI(){
        return new OpenAPI().info(new Info()
                .title("LibTrack API").version("1.0")
                .description("Library book tracking system"));
    }
}
