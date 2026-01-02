package com.churninsight.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "prediction_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false, length = 50)
    private String customerId;

    @Column(name = "tenure", nullable = false)
    private Integer tenure;

    @Column(name = "contract_type", nullable = false, length = 20)
    private String contractType;

    @Column(name = "subscription_type", nullable = false, length = 20)
    private String subscriptionType;

    @Column(name = "usage_time", nullable = false)
    private Double usageTime;

    @Column(name = "login_frequency", nullable = false)
    private Integer loginFrequency;

    @Column(name = "payment_record", nullable = false)
    private Integer paymentRecord;

    @Column(name = "total_spend", nullable = false)
    private Double totalSpend;

    // ⚠️ CORRECCIÓN: Hacer nullable porque solo se sabe después
    @Column(name = "actual_churn", length = 1)
    private String actualChurn;  // Puede ser null inicialmente

    @Column(name = "predicted_result", nullable = false, length = 20)
    private String predictedResult;

    @Column(name = "probability", nullable = false)
    private Double probability;

    @Column(name = "prediction_description", columnDefinition = "TEXT")
    private String predictionDescription;

    @Column(name = "prediction_timestamp", nullable = false)
    private LocalDateTime predictionTimestamp;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_prediction_correct")
    private Boolean isPredictionCorrect;

    @Column(name = "risk_level", length = 10)
    private String riskLevel;

    @Column(name = "model_version", length = 20)
    private String modelVersion;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    // ========== MÉTODOS MEJORADOS ==========
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        // Asegurar que predictionTimestamp no sea null
        if (this.predictionTimestamp == null) {
            this.predictionTimestamp = LocalDateTime.now();
        }

        // Calcular solo si tenemos los datos necesarios
        if (this.probability != null) {
            this.riskLevel = calculateRiskLevel(this.probability);
            this.confidenceScore = calculateConfidenceScore();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();

        // Solo calcular si tenemos todos los datos necesarios
        if (this.actualChurn != null && this.predictedResult != null && this.probability != null) {
            this.isPredictionCorrect = calculatePredictionCorrectness();
            this.riskLevel = calculateRiskLevel(this.probability);
            this.confidenceScore = calculateConfidenceScore();
        }
    }

    // ========== MÉTODOS ESTÁTICOS PARA EVITAR PROBLEMAS ==========
    private Boolean calculatePredictionCorrectness() {
        if (actualChurn == null || predictedResult == null || probability == null) {
            return null;
        }

        boolean predictedChurn = "HIGH_RISK".equals(predictedResult) ||
                ("MEDIUM_RISK".equals(predictedResult) && probability > 0.6);
        boolean actuallyChurned = "1".equals(actualChurn);

        return (predictedChurn == actuallyChurned);
    }

    private static String calculateRiskLevel(Double probability) {
        if (probability == null) {
            return "UNKNOWN";
        }

        if (probability >= 0.8) return "HIGH";
        else if (probability >= 0.6) return "MEDIUM";
        else if (probability >= 0.4) return "MODERATE";
        else return "LOW";
    }

    private Double calculateConfidenceScore() {
        if (probability == null) {
            return 0.0;
        }

        double baseConfidence = probability;
        double tenureFactor = calculateTenureFactor(tenure);
        double dataFactor = calculateDataFactor(loginFrequency, usageTime);

        return Math.min(1.0, baseConfidence + tenureFactor + dataFactor);
    }

    private static double calculateTenureFactor(Integer tenure) {
        if (tenure == null) return 0.0;

        if (tenure > 60) return 0.15;
        if (tenure > 36) return 0.10;
        if (tenure > 12) return 0.05;
        return 0.0;
    }

    private static double calculateDataFactor(Integer loginFrequency, Double usageTime) {
        if (loginFrequency == null || usageTime == null) return 0.0;

        if (loginFrequency > 20 && usageTime > 50) return 0.10;
        if (loginFrequency > 10 || usageTime > 25) return 0.05;
        return 0.0;
    }

    // ========== MÉTODO FACTORY MEJORADO ==========
    public static PredictionRecord fromRequestAndResponse(
            String customerId, Integer tenure, String contractType,
            String subscriptionType, Double usageTime, Integer loginFrequency,
            Integer paymentRecord, Double totalSpend, String churn,
            String predictedResult, Double probability,
            String predictionDescription, LocalDateTime predictionTimestamp) {

        return PredictionRecord.builder()
                .customerId(customerId)
                .tenure(tenure)
                .contractType(contractType != null ? contractType.toUpperCase() : null)
                .subscriptionType(subscriptionType != null ? subscriptionType.toUpperCase() : null)
                .usageTime(usageTime)
                .loginFrequency(loginFrequency)
                .paymentRecord(paymentRecord)
                .totalSpend(totalSpend)
                .actualChurn(churn)  // Puede ser null
                .predictedResult(predictedResult)
                .probability(probability)
                .predictionDescription(predictionDescription)
                .predictionTimestamp(predictionTimestamp != null ? predictionTimestamp : LocalDateTime.now())
                .modelVersion("1.0.0")
                .build();
    }
}