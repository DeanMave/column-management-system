package io.github.deanmave.hplclims.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final NotFoundException e) {
        log.warn("Объект не найден: {}", e.getMessage());
        return Map.of("error", "Искомый объект не найден.", "message", e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(final ValidationException e) {
        log.warn("Ошибка при валидации данных: {}", e.getMessage());
        return Map.of("error", "Ошибка при валидации данных.", "message", e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflict(final ConflictException e) {
        log.warn("Ошибка: конфликт данных: {}", e.getMessage());
        return Map.of("error", "Конфликт данных.", "message", e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDataIntegrity(final DataIntegrityViolationException e) {
        log.warn("Ошибка целостности данных: {}", e.getMessage());
        return Map.of("error", "Нарушение целостности данных. Возможно, значение уже используется.", "message", e.getMessage());
    }
}
