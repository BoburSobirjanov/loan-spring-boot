package uz.com.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.com.model.BaseModel;
import uz.com.model.enums.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity(name = "loans")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoansEntity extends BaseModel {

    @Column(nullable = false)
    BigDecimal amount;

    @Column(nullable = false)
    Integer interestRate;

    @Enumerated(value = EnumType.STRING)
    LoanStatus status;

    @JsonFormat(pattern = "yyyy.MM.dd")
    LocalDate dueDate;

    UUID changeStatusBy;

    @ManyToOne
    UserEntity user;
}
