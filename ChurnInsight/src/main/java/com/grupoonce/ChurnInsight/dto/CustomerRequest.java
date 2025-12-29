package com.churninsight.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

    @NotBlank(message = "customer_id es requerido")
    @JsonProperty("customer_id")
    private String customerId;

    @NotNull(message = "tenure es requerido")
    @Min(value = 0, message = "tenure debe ser mayor o igual a 0")
    @JsonProperty("tenure")
    private Integer tenure;

    @NotBlank(message = "contract_type es requerido")
    @JsonProperty("contract_type")
    private String contractType;

    @NotBlank(message = "subscription_type es requerido")
    @JsonProperty("subscription_type")
    private String subscriptionType;

    @NotNull(message = "usage_time es requerido")
    @PositiveOrZero(message = "usage_time debe ser mayor o igual a 0")
    @JsonProperty("usage_time")
    private Double usageTime;

    @NotNull(message = "login_frequency es requerido")
    @Min(value = 0, message = "login_frequency debe ser mayor o igual a 0")
    @JsonProperty("login_frequency")
    private Integer loginFrequency;

    @NotNull(message = "payment_record es requerido")
    @Min(value = 0, message = "payment_record debe ser mayor o igual a 0")
    @JsonProperty("payment_record")
    private Integer paymentRecord;

    @NotNull(message = "total_spend es requerido")
    @Positive(message = "total_spend debe ser positivo")
    @JsonProperty("total_spend")
    private Double totalSpend;

    @NotNull(message = "churn es requerido")
    @Pattern(regexp = "[01]", message = "churn debe ser '0' o '1'")
    @JsonProperty("churn")
    private String churn;  // "0" o "1"
}