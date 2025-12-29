package com.grupoonce.ChurnInsight.dto;

package com.churninsight.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
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
    private LocalDateTime timestamp;

    // Constructor
    public PredictionResponse(String customerId, String prediction,
                              Double probability, String description) {
        this.customerId = customerId;
        this.prediction = prediction;
        this.probability = probability;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }
}