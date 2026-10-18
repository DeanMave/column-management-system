package io.github.deanmave.hplclims.domain.mapper;

import io.github.deanmave.hplclims.domain.User;
import io.github.deanmave.hplclims.domain.dto.request.UserCreateDto;
import io.github.deanmave.hplclims.domain.dto.request.UserUpdateDto;
import io.github.deanmave.hplclims.domain.dto.response.UserResponseDto;
import io.github.deanmave.hplclims.domain.dto.response.UserShortResponseDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserResponseDto toUserResponseDto(User user);

    UserShortResponseDto toUserShortResponseDto(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(UserCreateDto dto);

    @Mapping(target = "id", ignore = true)
    User updateFromDto(@MappingTarget User user, UserUpdateDto dto);
}
