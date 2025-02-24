package main.compilations.service;

import main.compilations.dto.CompilationRequestDto;
import main.compilations.dto.CompilationResponseDto;
import main.compilations.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationResponseDto createCompilation(CompilationRequestDto compilationDto);

    CompilationResponseDto updateCompilation(Long compId, NewCompilationDto update);

    void deleteCompilation(Long compId);

    List<CompilationResponseDto> getCompilation(Boolean pinned, Integer from, Integer size);

    CompilationResponseDto findCompilationById(Long compId);
}