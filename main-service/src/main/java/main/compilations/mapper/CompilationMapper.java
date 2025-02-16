package main.compilations.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import main.compilations.dto.CompilationRequestDto;
import main.compilations.dto.CompilationResponseDto;
import main.compilations.model.Compilation;
import main.events.mapper.EventMapper;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {
    public CompilationResponseDto toDto(Compilation compilation) {
        return CompilationResponseDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents().stream()
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toSet()))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static Compilation toCompilation(CompilationRequestDto compilationDto) {
        return Compilation.builder()
                .pinned(compilationDto.getPinned())
                .title(compilationDto.getTitle())
                .build();
    }
}