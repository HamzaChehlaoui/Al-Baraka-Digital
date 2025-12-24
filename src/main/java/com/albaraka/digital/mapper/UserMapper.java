package com.albaraka.digital.mapper;

import com.albaraka.digital.dto.response.UserDto;
import com.albaraka.digital.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);
}
