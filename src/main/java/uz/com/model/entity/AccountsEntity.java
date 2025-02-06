package uz.com.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.com.model.BaseModel;
import uz.com.model.enums.AccountType;

import java.math.BigDecimal;

@Entity(name = "accounts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountsEntity extends BaseModel {

    BigDecimal balance;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    AccountType type;

    @Column(nullable = false)
    Integer interestRate;

    @ManyToOne
    UserEntity user;
}
