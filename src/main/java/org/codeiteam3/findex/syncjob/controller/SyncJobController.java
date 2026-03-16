package org.codeiteam3.findex.syncjob.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.codeiteam3.findex.syncjob.dto.IndexDataSyncRequestDto;
import org.codeiteam3.findex.syncjob.dto.SyncJobDto;
import org.codeiteam3.findex.syncjob.service.SyncJobService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/sync-jobs")
public class SyncJobController {
    private final SyncJobService syncJobService;

    @PostMapping("/index-infos")
    public List<SyncJobDto> createIndexInfoSyncJob(HttpServletRequest request) {
        return syncJobService.indexInfoSyncJob(request.getRemoteAddr());
    }

    @PostMapping("/index-data")
    public List<SyncJobDto> createIndexDataSyncJob(
            HttpServletRequest request,
            @RequestBody IndexDataSyncRequestDto requestDto
    ){
        return syncJobService.indexDataSyncJob(request.getRemoteAddr(), requestDto);
    }
}
