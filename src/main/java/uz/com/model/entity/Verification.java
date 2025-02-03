package uz.com.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "verifications")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    LocalDateTime createdAt;

    UUID to_to;

    Integer code;

}
