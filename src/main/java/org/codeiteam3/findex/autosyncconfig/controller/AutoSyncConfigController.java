package org.codeiteam3.findex.autosyncconfig.controller;

import org.codeiteam3.findex.autosyncconfig.dto.AutoSyncConfigUpdateRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/api/v1/auto-sync-configs")
public class AutoSyncConfigController {
    @PatchMapping("/{id}")
    public ResponseEntity<AutoSyncConfigController> update(
            @RequestBody AutoSyncConfigUpdateRequestDto requestDto,
            @PathVariable("id") UUID id
    ) {}
}
