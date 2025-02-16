package main.users.mapper;

import lombok.*;
import main.users.dto.UserCreateRequestDto;
import main.users.dto.UserDto;
import main.users.dto.UserShortDto;
import main.users.model.User;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDto toUserOutDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserCreateRequestDto userCreateRequestDto) {
        User user = new User(null,
                userCreateRequestDto.getName(),
                userCreateRequestDto.getEmail());
        return user;
    }

    public static UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}