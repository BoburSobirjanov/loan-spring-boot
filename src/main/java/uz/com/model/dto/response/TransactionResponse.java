package uz.com.model.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.com.model.enums.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionResponse {

    UUID id;
    BigDecimal amount;
    TransactionType type;
    AccountResponse account;
}
