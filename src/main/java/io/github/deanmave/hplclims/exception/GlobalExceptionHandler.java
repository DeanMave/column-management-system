package io.github.deanmave.hplclims.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(NotFoundException e, Model model) {
        log.warn("Объект не найден: {}", e.getMessage());
        model.addAttribute("message", e.getMessage());
        return "error/not-found";  // templates/error/not-found.html
    }

    @ExceptionHandler(ValidationException.class)
    public String handleValidation(ValidationException e, Model model) {
        log.warn("Ошибка при валидации данных: {}", e.getMessage());
        model.addAttribute("message", e.getMessage());
        return "error/validation";  // templates/error/validation.html
    }

    @ExceptionHandler(ConflictException.class)
    public String handleConflict(ConflictException e, Model model) {
        log.warn("Ошибка: конфликт данных: {}", e.getMessage());
        model.addAttribute("message", e.getMessage());
        return "error/conflict";  // templates/error/conflict.html
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrity(DataIntegrityViolationException e, Model model) {
        log.warn("Ошибка целостности данных: {}", e.getMessage());
        model.addAttribute("message", "Нарушение целостности данных. Возможно, значение уже используется.");
        return "error/conflict";
    }

}
