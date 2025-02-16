package main.users.controller;

import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import main.users.dto.UserCreateRequestDto;
import main.users.dto.UserDto;
import main.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService service;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Запрос на получение списка пользователей");

        return service.get(ids, from, size);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUsers(@RequestBody @Valid UserCreateRequestDto userCreateRequestDto) {
        log.info("Создание User: {}", userCreateRequestDto);
        UserDto newUserDto = service.create(userCreateRequestDto);
        log.info("Создан User: {}", newUserDto);
        return newUserDto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUsers(@PathVariable long id) {
        log.info("Удалениее User по: {}", id);
        service.delete(id);
        log.info("Успешно удален");
    }
}