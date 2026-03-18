package org.codeiteam3.findex.syncjob.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.codeiteam3.findex.pagination.CursorPageResponse;
import org.codeiteam3.findex.enums.JobType;
import org.codeiteam3.findex.enums.Result;
import org.codeiteam3.findex.syncjob.dto.IndexDataSyncRequestDto;
import org.codeiteam3.findex.syncjob.dto.SyncJobDto;
import org.codeiteam3.findex.syncjob.service.SyncJobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/sync-jobs")
public class SyncJobController {
    private final SyncJobService syncJobService;

    @PostMapping("/index-infos")
    public ResponseEntity<List<SyncJobDto>> createIndexInfoSyncJob(HttpServletRequest request) {
        return ResponseEntity.status(201).body(syncJobService.indexInfoSyncJob(request.getRemoteAddr()));
    }

    @PostMapping("/index-data")
    public ResponseEntity<List<SyncJobDto>> createIndexDataSyncJob(
            HttpServletRequest request,
            @RequestBody IndexDataSyncRequestDto requestDto
    ){
        return ResponseEntity.status(201).body(syncJobService.indexDataSyncJob(request.getRemoteAddr(), requestDto));
    }

    @GetMapping
    public ResponseEntity<CursorPageResponse<SyncJobDto>> findAll(
            @RequestParam(required = false) JobType jobType, //유형
            @RequestParam(required = false) UUID indexInfoId, //지수정보
            @RequestParam(required = false) LocalDate baseDateFrom, //대상날짜
            @RequestParam(required = false) LocalDate baseDateTo,
            @RequestParam(required = false) String worker,//작업자
            @RequestParam(required = false) LocalDateTime jobTimeFrom,//작업일시
            @RequestParam(required = false) LocalDateTime jobTimeTo,
            @RequestParam(required = false) Result status,//결과
            @RequestParam(required = false) UUID idAfter,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "jobTime") String sortField,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        return ResponseEntity.status(200).body(syncJobService.findAll(
                jobType,
                indexInfoId,
                worker,
                jobTimeFrom,
                jobTimeTo,
                status,
                idAfter,
                cursor,
                sortField,
                sortDirection,
                size
        ));
    }
}