package Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity(name="Entrada")
@Table(name="entradas")
public class Entrada {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long customer_id;
    private Integer tenure;
    private String contract_type;
    private String subscription_type;
    private Double usage_time;
    private Integer login_frequency;
    private Integer payment_record;
    private Double total_spend;
    private String churn;

    public Entrada(CustomerRequest cr){
        
    }


}
