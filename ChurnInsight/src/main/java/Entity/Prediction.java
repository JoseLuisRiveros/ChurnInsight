package Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity(name="Prediction")
@Table(name="predictions")
public class Prediction {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private String customer_id;
    private String prediction;
    private Double probability;
    private String description;
    private LocalDateTime timestamp;

    public Prediction(PredictionResponse pr){
        this.customer_id = null;
        this.prediction = pr.prediction();
        this.probability = pr.probability();
        this.description = pr.description();
        this.timestamp = pr.timestamp();
    }
}
