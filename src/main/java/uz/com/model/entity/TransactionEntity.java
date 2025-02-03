package uz.com.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.com.model.BaseModel;
import uz.com.model.enums.TransactionType;

import java.math.BigDecimal;

@Entity(name = "transactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionEntity extends BaseModel {

    @Column(nullable = false)
    @NotBlank(message = "amount is required!")
    BigDecimal amount;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    @NotBlank(message = "type is required!")
    TransactionType type;

    @ManyToOne
    AccountsEntity account;
}
