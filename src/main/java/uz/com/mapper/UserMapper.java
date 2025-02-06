package uz.com.mapper;

import org.mapstruct.Mapper;
import uz.com.model.dto.request.UserCreateRequest;
import uz.com.model.dto.response.UserResponse;
import uz.com.model.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper{

    UserResponse toResponse(UserEntity entity);
    UserEntity toEntity(UserCreateRequest request);

}
