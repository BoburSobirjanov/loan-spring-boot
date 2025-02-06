package uz.com.model.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditCreateRequest {

    String httpMethod;

    String apiEndpoint;

    String request;

    String response;

    int statusCode;

    String userId;
}
