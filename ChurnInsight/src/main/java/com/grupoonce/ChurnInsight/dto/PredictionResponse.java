package com.churninsight.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResponse {

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("prediction")
    private String prediction;

    @JsonProperty("probability")
    private Double probability;

    @JsonProperty("description")
    private String description;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime timestamp;

    // Método de conveniencia (opcional)
    public static PredictionResponse create(String customerId, String prediction,
                                            Double probability, String description) {
        return PredictionResponse.builder()
                .customerId(customerId)
                .prediction(prediction)
                .probability(probability)
                .description(description)
                .timestamp(LocalDateTime.now())
                .build();
    }
}