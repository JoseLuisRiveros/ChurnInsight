package com.grupoonce.ChurnInsight.service;

import com.grupoonce.ChurnInsight.dto.CustomerRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service encargado exclusivamente de la integración con el modelo ML (Python).
 * - Envía el CustomerRequest tal cual (las propiedades están en snake_case por @JsonProperty)
 * - Espera una respuesta JSON con la clave "probability" (Double).
 *
 * Configurar en application.properties:
 * ml.api.url=http://localhost:8000   (o la URL donde esté la API de Python)
 */
@Service
public class ModelIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(ModelIntegrationService.class);

    private final RestTemplate restTemplate;

    // URL base del servicio ML (p. ej. http://localhost:8000)
    @Value("${ml.api.url}")
    private String mlApiUrl;

    // Endpoint que expone el modelo: POST {mlApiUrl}/predict
    private static final String PREDICT_PATH = "/predict";

    public ModelIntegrationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Llama al endpoint /predict del servicio ML y devuelve la probabilidad de churn.
     *
     * @param request DTO con los datos del cliente (se serializa a JSON).
     * @return probabilidad de churn en [0.0, 1.0]
     */
    public Double getChurnProbability(CustomerRequest request) {
        String url = mlApiUrl + PREDICT_PATH;
        try {
            // Mapeamos la respuesta a MLResponse (clase privada más abajo)
            ResponseEntity<MLResponse> response =
                    restTemplate.postForEntity(url, request, MLResponse.class);

            MLResponse body = response.getBody();

            if (body == null || body.getProbability() == null) {
                log.error("Respuesta inválida del servicio ML");
                throw new RuntimeException("Respuesta inválida del modelo ML");
            }

            Double prob = body.getProbability();

            // Validación del rango
            if (prob < 0.0 || prob > 1.0) {
                log.warn("Probabilidad fuera de rango recibida del ML: {}", prob);
                // Normalizamos/clamp por seguridad
                prob = Math.max(0.0, Math.min(1.0, prob));
            }

            return prob;

        } catch (RestClientException ex) {
            // Capturamos errores de conexión / timeout / 5xx del servicio ML
            log.error("Error al llamar al servicio ML en {}: {}", url, ex.getMessage());
            throw new RuntimeException("No se pudo conectar con el servicio de ML", ex);
        }
    }

    /**
     * Clase privada para mapear la respuesta JSON del servicio ML.
     * Ejemplo esperado:
     * {
     *   "probability": 0.82
     * }
     *
     * Si API ML devuelve otra estructura, ajustar esta clase.
     */
    private static class MLResponse {
        @JsonProperty("probability")
        private Double probability;

        public Double getProbability() {
            return probability;
        }

        public void setProbability(Double probability) {
            this.probability = probability;
        }
    }
}
