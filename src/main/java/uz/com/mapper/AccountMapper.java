package uz.com.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uz.com.model.dto.request.AccountCreateRequest;
import uz.com.model.dto.response.AccountResponse;
import uz.com.model.entity.AccountsEntity;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface AccountMapper {

    AccountResponse toResponse(AccountsEntity entity);

    @Mapping(source = "userId", target = "user.id", qualifiedByName = "stringToUUID")
    AccountsEntity toEntity(AccountCreateRequest request);

    @Named("stringToUUID")
    static UUID stringToUUID(String userId) {
        return userId != null ? UUID.fromString(userId) : null;
    }
}
