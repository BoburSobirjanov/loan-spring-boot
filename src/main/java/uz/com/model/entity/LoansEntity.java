package uz.com.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.com.model.BaseModel;
import uz.com.model.enums.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "loans")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoansEntity extends BaseModel {

    @Column(nullable = false)
    @NotBlank(message = "amount is required!")
    BigDecimal amount;

    @Column(nullable = false)
    @NotBlank(message = "interest rate is required!")
    Integer interestRate;

    @Enumerated(value = EnumType.STRING)
    LoanStatus status;

    @JsonFormat(pattern = "yyyy.MM.dd")
    @NotBlank(message = "due date is required!")
    LocalDate dueDate;

    @ManyToOne
    UserEntity user;
}
