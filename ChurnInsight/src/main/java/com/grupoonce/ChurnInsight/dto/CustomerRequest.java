package com.grupoonce.ChurnInsight.dto;

package com.churninsight.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import jdk.jfr.DataAmount;
import lombok.Data;

@Data
public class CustomerRequest {

    @NotBlank(message = "El customer_id es obligatorio")
    @JsonProperty("customer_id")
    private String customerId;

    @NotNull(message = "El tenure_months es obligatorio")
    @Min(value = 0, message = "El tenure_months debe ser mayor o igual a 0")
    @JsonProperty("tenure_months")
    private Integer tenureMonths;

    @NotNull(message = "El monthly_charges es obligatorio")
    @Positive(message = "El monthly_charges debe ser positivo")
    @JsonProperty("monthly_charges")
    private Double monthlyCharges;

    @NotNull(message = "El total_charges es obligatorio")
    @Positive(message = "El total_charges debe ser positivo")
    @JsonProperty("total_charges")
    private Double totalCharges;

    @NotNull(message = "El payment_delays es obligatorio")
    @Min(value = 0, message = "El payment_delays debe ser mayor o igual a 0")
    @JsonProperty("payment_delays")
    private Integer paymentDelays;

    @NotNull(message = "El avg_monthly_usage_hours es obligatorio")
    @PositiveOrZero(message = "El avg_monthly_usage_hours debe ser mayor o igual a 0")
    @JsonProperty("avg_monthly_usage_hours")
    private Double avgMonthlyUsageHours;

    @NotBlank(message = "El plan_type es obligatorio")
    @JsonProperty("plan_type")
    private String planType;

    @NotBlank(message = "El contract_type es obligatorio")
    @JsonProperty("contract_type")
    private String contractType;
}
