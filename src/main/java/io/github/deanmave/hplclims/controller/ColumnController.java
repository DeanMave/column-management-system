package io.github.deanmave.hplclims.controller;

import io.github.deanmave.hplclims.domain.ColumnStatus;
import io.github.deanmave.hplclims.domain.dto.request.ColumnCreateDto;
import io.github.deanmave.hplclims.domain.dto.request.ColumnUpdateDto;
import io.github.deanmave.hplclims.domain.dto.response.ColumnResponseDto;
import io.github.deanmave.hplclims.service.interfaces.ColumnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/columns")
@RequiredArgsConstructor
public class ColumnController {
    private final ColumnService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ColumnResponseDto addNewColumn(@Valid @RequestBody ColumnCreateDto createDto) {
        return service.create(createDto);
    }

    @GetMapping
    public List<ColumnResponseDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{columnId}")
    public ColumnResponseDto getById(@PathVariable Long columnId) {
        return service.getById(columnId);
    }

    @DeleteMapping("/{columnId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long columnId) {
        service.deleteById(columnId);
    }

    @PatchMapping("/{columnId}/status")
    public ColumnResponseDto changeStatus(@PathVariable Long columnId,
                                          @RequestParam ColumnStatus status) {
        return service.changeStatus(columnId, status);
    }

    @PatchMapping("/{columnId}")
    public ColumnResponseDto updateColumn(@PathVariable Long columnId,
                                          @Valid @RequestBody ColumnUpdateDto updateDto) {
        return service.correctData(columnId, updateDto);
    }
}
