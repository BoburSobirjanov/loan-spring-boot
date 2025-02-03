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
    Integer interestRate;
    String status;
    String dueDate;
    String userId;

}
