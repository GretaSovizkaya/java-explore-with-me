package main.compilations.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.compilations.dto.CompilationRequestDto;
import main.compilations.dto.CompilationResponseDto;
import main.compilations.dto.NewCompilationDto;
import main.compilations.mapper.CompilationMapper;
import main.compilations.model.Compilation;
import main.compilations.repository.CompilationRepository;
import main.events.model.Event;
import main.events.repository.EventRepository;
import main.exceptions.NotFoundException;
import main.exceptions.ValidatetionConflict;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CompilationServiceImpl implements CompilationService {
    CompilationRepository compilationRepository;
    EventRepository eventRepository;
    CompilationMapper compilationMapper;

    @Transactional
    @Override
    public CompilationResponseDto createCompilation(CompilationRequestDto compilationDto) {
        Compilation compilation = compilationMapper.toCompilation(compilationDto);
        compilation.setPinned(Optional.ofNullable(compilation.getPinned()).orElse(false));

        Set<Long> compEventIds = (compilationDto.getEvents() != null) ? compilationDto.getEvents() : Collections.emptySet();
        List<Long> eventIds = new ArrayList<>(compEventIds);
        List<Event> events = eventRepository.findAllByIdIn(eventIds);
        Set<Event> eventsSet = new HashSet<>(events);
        compilation.setEvents(eventsSet);

        Compilation compilationAfterSave = compilationRepository.save(compilation);
        return compilationMapper.toDto(compilationAfterSave);
    }

    @Transactional
    @Override
    public CompilationResponseDto updateCompilation(Long compId, NewCompilationDto update) {
        Compilation compilation = checkCompilation(compId);

        Set<Long> eventIds = update.getEvents();

        if (eventIds != null) {
            List<Event> events = eventRepository.findAllByIdIn(new ArrayList<>(eventIds));
            Set<Event> eventSet = new HashSet<>(events);
            compilation.setEvents(eventSet);
        }

        compilation.setPinned(Optional.ofNullable(update.getPinned()).orElse(compilation.getPinned()));
        if (compilation.getTitle().isBlank()) {
            throw new ValidatetionConflict("Title не может состоять из пробелов");
        }
        compilation.setTitle(Optional.ofNullable(update.getTitle()).orElse(compilation.getTitle()));

        return compilationMapper.toDto(compilation);
    }

    @Transactional
    @Override
    public void deleteCompilation(Long compId) {
        checkCompilation(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationResponseDto> getCompilation(Boolean pinned, Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from, size);
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAll(pageRequest).getContent();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
        }

        return compilations.stream()
                .map(compilationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationResponseDto findCompilationById(Long compId) {
        return compilationMapper.toDto(checkCompilation(compId));
    }

    private Compilation checkCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation с id = " + compId + " не найден");
        }
        return compilationRepository.findById(compId).orElse(null);
    }
}