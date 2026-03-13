package org.codeiteam3.findex.indexdata.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.enums.PeriodType;
import org.codeiteam3.findex.indexdata.dto.IndexChartDto;
import org.codeiteam3.findex.indexdata.service.IndexDashBoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/index-data")
@Tag(name = "지수 대쉬보드 API", description = "지수 대쉬보드 관리 API")
public class IndexDashBoardController {
    private final IndexDashBoardService indexDashBoardService;

    @GetMapping(value = "/{id}/chart")
    public ResponseEntity<IndexChartDto> findIndexChart(@PathVariable("id") UUID indexInfoId,
                                         @RequestParam(required = false) PeriodType periodType){
        IndexChartDto response = indexDashBoardService.find(indexInfoId, periodType);
        return ResponseEntity.ok(response);
    }
}
