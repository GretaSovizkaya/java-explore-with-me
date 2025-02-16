package main.users.service;

import main.users.dto.UserCreateRequestDto;
import main.users.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> get(List<Long> ids, Integer from, Integer size);

    UserDto create(UserCreateRequestDto inDto);

    void delete(Long userId);

}