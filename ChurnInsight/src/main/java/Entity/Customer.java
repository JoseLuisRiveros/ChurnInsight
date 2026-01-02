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
@Entity(name="Customer")
@Table(name="customers")
public class Customer {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private String customer_id;
    private Integer tenure;
    private String contract_type;
    private String subscription_type;
    private Double usage_time;
    private Integer login_frequency;
    private Integer payment_record;
    private Double total_spend;
    private String churn;

    public Customer(CustomerRequest cr){
        this.customer_id = null;
        this.tenure=cr.tenure();
        this.contract_type=cr.contract();
        this.subscription_type = cr.subscription_type();
        this.usage_time = cr.usage_time();
        this.login_frequency = cr.login_frequency();
        this.payment_record = cr.payment_record();
        this.total_spend = cr.total_spend();
        this.churn = cr.churn();
    }


}
