package org.codeiteam3.findex.sync_job.controller;

import lombok.AllArgsConstructor;
import org.codeiteam3.findex.sync_job.dto.SyncJobDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/sync-jobs")
public class SyncJobController {
    @GetMapping("/index-infos")
    public SyncJobDto getIndexInfos() {
        return null;
    }
}
