package uz.com.model.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtResponse {

    String accessToken;
    String refreshToken;
    UserResponse userResponse;
}
