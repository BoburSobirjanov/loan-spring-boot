package uz.com.model.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.com.model.enums.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountResponse {

    UUID id;

    BigDecimal balance;

    AccountType type;

    Integer interestRate;

    UserResponse user;
}
