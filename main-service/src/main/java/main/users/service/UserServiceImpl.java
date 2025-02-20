package main.users.service;

import lombok.RequiredArgsConstructor;
import main.exceptions.NotFoundException;
import main.users.dto.UserCreateRequestDto;
import main.users.dto.UserDto;
import main.users.mapper.UserMapper;
import main.users.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto create(UserCreateRequestDto userCreateRequestDto) {
        return UserMapper.toUserOutDto(userRepository.save(UserMapper.toUser(userCreateRequestDto)));
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id= " + userId + " не найден");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> get(List<Long> listId, Integer from, Integer size) {
        List<UserDto> listUserResponseDto;

        PageRequest page = PageRequest.of(from / size, size);
        if (listId != null) {
            listUserResponseDto = userRepository.findByIdIn(listId, page).stream()
                    .map(UserMapper::toUserOutDto)
                    .collect(Collectors.toList());
        } else {
            listUserResponseDto = userRepository.findAll(page).stream()
                    .map(UserMapper::toUserOutDto)
                    .collect(Collectors.toList());
        }
        return listUserResponseDto;
    }

}