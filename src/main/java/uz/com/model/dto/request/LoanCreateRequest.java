package uz.com.model.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanCreateRequest {

    BigDecimal amount;
    Double interestRate;
    Integer months;
    String userId;

}
