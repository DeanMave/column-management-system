package io.github.deanmave.hplclims.controller;

import io.github.deanmave.hplclims.domain.User;
import io.github.deanmave.hplclims.domain.dto.request.*;
import io.github.deanmave.hplclims.domain.dto.response.UsageLogResponseDto;
import io.github.deanmave.hplclims.service.interfaces.UsageLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/usage-logs")
@RequiredArgsConstructor
public class UsageLogController {
    private final UsageLogService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'SENIOR_USER', 'USER')")
    @PostMapping("/start")
    @ResponseStatus(HttpStatus.CREATED)
    public UsageLogResponseDto startUsage(@AuthenticationPrincipal User currentUser,
                                          @Valid @RequestBody StartUsageRequest request) {
        return service.startUsage(currentUser.getId(), request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SENIOR_USER', 'USER')")
    @PatchMapping("/{logId}/end")
    public UsageLogResponseDto endUsage(@PathVariable Long logId,
                                        @Valid @RequestBody EndUsageRequest request) {
        return service.endUsage(logId, request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SENIOR_USER', 'USER')")
    @PatchMapping("/{logId}/reject")
    public UsageLogResponseDto rejectUsage(@PathVariable Long logId,
                                           @Valid @RequestBody RejectUsageRequest request) {
        return service.rejectUsage(logId, request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SENIOR_USER', 'USER')")
    @PatchMapping("/{logId}")
    public UsageLogResponseDto updateLog(@PathVariable Long logId,
                                         @Valid @RequestBody CorrectUsageLogRequest request) {
        return service.correctLog(logId, request);
    }

    @GetMapping("/column/{columnId}")
    public List<UsageLogResponseDto> getLogsByColumn(@PathVariable Long columnId) {
        return service.getLogsByColumn(columnId);
    }

    @GetMapping("/user/{userId}")
    public List<UsageLogResponseDto> getLogsByUser(@PathVariable Long userId) {
        return service.getLogsByUser(userId);
    }

    @GetMapping("/active")
    public List<UsageLogResponseDto> getActiveLogs() {
        return service.getActiveUsages();
    }

    @PreAuthorize("hasAnyRole('LAB_HEAD','SENIOR_USER')")
    @DeleteMapping("/{logId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long logId) {
        service.deleteById(logId);
    }
}
