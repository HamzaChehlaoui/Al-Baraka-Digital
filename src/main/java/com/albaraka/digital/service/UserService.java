package com.albaraka.digital.service;

import com.albaraka.digital.dto.request.UserRequest;
import com.albaraka.digital.dto.response.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    UserDto createUser(UserRequest request);

    UserDto updateUser(Long id, UserRequest request);

    void deleteUser(Long id);

    void toggleUserStatus(Long id);
}
