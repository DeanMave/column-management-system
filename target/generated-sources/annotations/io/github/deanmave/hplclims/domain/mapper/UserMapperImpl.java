package io.github.deanmave.hplclims.domain.mapper;

import io.github.deanmave.hplclims.domain.User;
import io.github.deanmave.hplclims.domain.dto.request.UserCreateDto;
import io.github.deanmave.hplclims.domain.dto.request.UserUpdateDto;
import io.github.deanmave.hplclims.domain.dto.response.UserResponseDto;
import io.github.deanmave.hplclims.domain.dto.response.UserShortResponseDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-09T18:08:27+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12.1 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponseDto toUserResponseDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.setId( user.getId() );
        userResponseDto.setFirstName( user.getFirstName() );
        userResponseDto.setLastName( user.getLastName() );
        userResponseDto.setMiddleName( user.getMiddleName() );
        userResponseDto.setLogin( user.getLogin() );
        userResponseDto.setRole( user.getRole() );
        userResponseDto.setPosition( user.getPosition() );
        userResponseDto.setActive( user.isActive() );

        return userResponseDto;
    }

    @Override
    public UserShortResponseDto toUserShortResponseDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserShortResponseDto userShortResponseDto = new UserShortResponseDto();

        userShortResponseDto.setId( user.getId() );
        userShortResponseDto.setFirstName( user.getFirstName() );
        userShortResponseDto.setLastName( user.getLastName() );
        userShortResponseDto.setMiddleName( user.getMiddleName() );

        return userShortResponseDto;
    }

    @Override
    public User toUser(UserCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        User user = new User();

        user.setFirstName( dto.getFirstName() );
        user.setLastName( dto.getLastName() );
        user.setMiddleName( dto.getMiddleName() );
        user.setLogin( dto.getLogin() );
        user.setPassword( dto.getPassword() );
        user.setRole( dto.getRole() );
        user.setPosition( dto.getPosition() );

        return user;
    }

    @Override
    public User updateFromDto(User user, UserUpdateDto dto) {
        if ( dto == null ) {
            return user;
        }

        if ( dto.getFirstName() != null ) {
            user.setFirstName( dto.getFirstName() );
        }
        if ( dto.getLastName() != null ) {
            user.setLastName( dto.getLastName() );
        }
        if ( dto.getMiddleName() != null ) {
            user.setMiddleName( dto.getMiddleName() );
        }
        if ( dto.getLogin() != null ) {
            user.setLogin( dto.getLogin() );
        }
        if ( dto.getRole() != null ) {
            user.setRole( dto.getRole() );
        }
        if ( dto.getPosition() != null ) {
            user.setPosition( dto.getPosition() );
        }

        return user;
    }
}
