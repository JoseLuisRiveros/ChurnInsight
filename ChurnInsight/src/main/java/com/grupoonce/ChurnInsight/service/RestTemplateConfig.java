package com.grupoonce.ChurnInsight.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración mínima para exponer un RestTemplate como bean.
 * Usamos RestTemplate por simplicidad (apto para un MVP).
 * Si prefieren WebClient (reactivo) pueden cambiarlo luego.
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
