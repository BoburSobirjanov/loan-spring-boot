package uz.com.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uz.com.model.dto.request.TransactionCreateRequest;
import uz.com.model.dto.response.TransactionResponse;
import uz.com.model.entity.TransactionEntity;

import java.util.UUID;


@Mapper(componentModel = "spring", uses = {AccountMapper.class})
public interface TransactionMapper {

    TransactionResponse toResponse(TransactionEntity entity);

    @Mapping(source = "accountId", target = "account.id", qualifiedByName = "stringToUUID1")
    TransactionEntity toEntity(TransactionCreateRequest request);

    @Named("stringToUUID1")
    static UUID stringToUUID1(String accountId) {
        return accountId != null ? UUID.fromString(accountId) : null;
    }
}
