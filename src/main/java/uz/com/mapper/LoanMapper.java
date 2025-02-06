package uz.com.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uz.com.model.dto.request.LoanCreateRequest;
import uz.com.model.dto.response.LoanResponse;
import uz.com.model.entity.LoansEntity;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface LoanMapper {

    LoanResponse toResponse(LoansEntity entity);

    @Mapping(source = "userId", target = "user.id", qualifiedByName = "stringToUUID")
    LoansEntity toEntity(LoanCreateRequest request);

    @Named("stringToUUID")
    static UUID stringToUUID(String userId) {
        return userId != null ? UUID.fromString(userId) : null;
    }
}
