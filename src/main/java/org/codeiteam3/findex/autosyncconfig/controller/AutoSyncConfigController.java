package org.codeiteam3.findex.autosyncconfig.controller;

import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.autosyncconfig.dto.AutoSyncConfigResponseDto;
import org.codeiteam3.findex.autosyncconfig.dto.AutoSyncConfigUpdateRequestDto;
import org.codeiteam3.findex.autosyncconfig.service.AutoSyncConfigService;
import org.codeiteam3.findex.pagination.CursorPageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/api/auto-sync-configs")
@RequiredArgsConstructor
public class AutoSyncConfigController {
    private final AutoSyncConfigService autoSyncConfigService;

    @PatchMapping("/{id}")
    public ResponseEntity<AutoSyncConfigResponseDto> update(
            @RequestBody AutoSyncConfigUpdateRequestDto requestDto,
            @PathVariable UUID id
    ) {
        return ResponseEntity.status(200).body(autoSyncConfigService.update(id, requestDto));
    }

    @GetMapping
    public ResponseEntity<CursorPageResponse<AutoSyncConfigResponseDto>> findAll(
        @RequestParam(required = false) UUID indexInfoId,
        @RequestParam(required = false) Boolean enabled,
        @RequestParam(required = false) UUID idAfter,
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false, defaultValue = "indexInfo.indexName") String sortField,
        @RequestParam(required = false, defaultValue = "asc") String sortDirection,
        @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.status(200).body(autoSyncConfigService.findAll(indexInfoId, enabled, idAfter, cursor, sortField, sortDirection, size));
    }
}
