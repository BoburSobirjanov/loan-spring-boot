package uz.com.model.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.com.model.enums.Gender;
import uz.com.model.enums.UserRole;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    UUID id;
    String fullName;
    String email;
    String phone;
    String address;
    Gender gender;
    Set<UserRole> role;
}
