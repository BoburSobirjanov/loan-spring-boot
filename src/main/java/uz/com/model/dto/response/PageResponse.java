package uz.com.model.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {

    List<T> response;
    int pageCount;


    public static <T> PageResponse<T> ok(int pageCount, List<T> response) {
        return PageResponse.<T>builder()
                .pageCount(pageCount)
                .response(response)
                .build();
    }
}
