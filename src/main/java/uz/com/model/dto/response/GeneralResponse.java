package uz.com.model.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import uz.com.model.enums.Status;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeneralResponse<T> {

    Status status;

    String message;

    T data;

    public static <T> GeneralResponse<T> ok(String message, T data) {
        return GeneralResponse.<T>builder()
                .status(Status.SUCCESS)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> GeneralResponse<T> error(String message){
        return GeneralResponse.<T>builder()
                .status(Status.ERROR)
                .message(message)
                .build();
    }
}
