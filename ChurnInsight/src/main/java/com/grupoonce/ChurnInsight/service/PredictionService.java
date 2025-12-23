package com.grupoonce.ChurnInsight.service;

import com.grupoonce.ChurnInsight.dto.CustomerRequest;
import com.grupoonce.ChurnInsight.dto.PredictionResponse;
import com.grupoonce.ChurnInsight.model.Prediction;
import com.grupoonce.ChurnInsight.repository.PredictionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio central de negocio para las predicciones.
 * - Recibe el DTO del controller.
 * - Llama a ModelIntegrationService para obtener la probabilidad.
 * - Decide la etiqueta de predicción según un umbral.
 * - Persiste un registro de la predicción (repo JPA).
 * - Devuelve un PredictionResponse para enviarlo al cliente.
 *
 * NOTA: ajustar RISK_THRESHOLD según acuerdo de negocio.
 */
@Service
public class PredictionService {

    private static final Logger log = LoggerFactory.getLogger(PredictionService.class);

    // Umbral de negocio: si probability >= RISK_THRESHOLD -> "Va a cancelar"
    private static final double RISK_THRESHOLD = 0.7;

    private final ModelIntegrationService modelIntegrationService;
    private final PredictionRepository predictionRepository;

    public PredictionService(ModelIntegrationService modelIntegrationService,
                             PredictionRepository predictionRepository) {
        this.modelIntegrationService = modelIntegrationService;
        this.predictionRepository = predictionRepository;
    }

    /**
     * Orquesta el flujo completo de predicción.
     *
     * @param request DTO validado que viene del controller
     * @return PredictionResponse listo para devolver por el controller
     */
    public PredictionResponse predict(CustomerRequest request) {

        // 1) Llamar al servicio ML y obtener probabilidad
        Double probability = modelIntegrationService.getChurnProbability(request);
        log.debug("Probabilidad recibida del ML para customer {}: {}", request.getCustomerId(), probability);

        // 2) Determinar label de negocio (texto legible para front)
        String predictionLabel = (probability >= RISK_THRESHOLD)
                ? "Va a cancelar"
                : "Va a continuar";

        // 3) Descripción humana para el frontend / equipo de negocio
        String description = (probability >= RISK_THRESHOLD)
                ? "Cliente con alta probabilidad de cancelar el servicio"
                : "Cliente con baja probabilidad de cancelar el servicio";

        // 4) Construir la entidad Prediction y persistirla
        //    - Se asume que la entidad Prediction tiene setters estándar.
        //    - Si la entidad tiene otro constructor, ajustarlo.
        Prediction entity = new Prediction();
        entity.setCustomerId(request.getCustomerId());
        entity.setProbability(probability);
        entity.setPrediction(predictionLabel);
        // opcional: entity.setCreatedAt(LocalDateTime.now()); si la entidad lo necesita

        predictionRepository.save(entity);
        log.info("Predicción guardada para customer {} con prob={} y label={}",
                request.getCustomerId(), probability, predictionLabel);

        // 5) Construir y devolver el DTO de respuesta (usa el constructor que definido)
        return new PredictionResponse(
                request.getCustomerId(),
                predictionLabel,
                probability,
                description
        );
    }
}
