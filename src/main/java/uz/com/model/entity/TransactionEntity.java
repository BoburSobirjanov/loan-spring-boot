package uz.com.model.entity;

import jakarta.persistence.*;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionEntity extends BaseModel {

    @Column(nullable = false)
    BigDecimal amount;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    TransactionType type;

    @ManyToOne
    AccountsEntity account;
}
