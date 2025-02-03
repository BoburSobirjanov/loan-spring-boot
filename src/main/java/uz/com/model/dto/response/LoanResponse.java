package uz.com.model.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.com.model.enums.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanResponse {

    UUID id;
    BigDecimal amount;
    Integer interestRate;
    LoanStatus status;
    LocalDate dueDate;
    UserResponse user;
}
