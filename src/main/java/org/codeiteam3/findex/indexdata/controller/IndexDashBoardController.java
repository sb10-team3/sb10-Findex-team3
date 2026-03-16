package org.codeiteam3.findex.indexdata.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.enums.PeriodType;
import org.codeiteam3.findex.indexdata.dto.IndexChartDto;
import org.codeiteam3.findex.indexdata.dto.IndexPerformanceDto;
import org.codeiteam3.findex.indexdata.dto.RankedIndexPerformanceDto;
import org.codeiteam3.findex.indexdata.service.IndexDashBoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/index-data")
@Tag(name = "지수 대쉬보드 API", description = "지수 대쉬보드 관리 API")
public class IndexDashBoardController {
    private final IndexDashBoardService indexDashBoardService;

    @GetMapping(value = "/{id}/chart")
    public ResponseEntity<IndexChartDto> findIndexChart(@PathVariable("id") UUID indexInfoId,
                                         @RequestParam(required = false, defaultValue = "DAILY") PeriodType periodType){
        IndexChartDto response = indexDashBoardService.find(indexInfoId, periodType);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/perfomance/rank")
    public ResponseEntity<List<RankedIndexPerformanceDto>> findIndexPerformanceRank(
            @RequestParam(required = false) UUID indexInfoId,
            @RequestParam(required = false, defaultValue = "DAILY") PeriodType periodType,
            @RequestParam(required = false, defaultValue = "10") Integer limit){
        List<RankedIndexPerformanceDto> response =
                indexDashBoardService.findIndexPerformanceRank(indexInfoId, periodType, limit);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/performance/favorite")
    public ResponseEntity<List<IndexPerformanceDto>> findFavoirteIndexPerformance(
            @RequestParam(required = false, defaultValue = "DAILY") PeriodType periodType){
        List<IndexPerformanceDto> response =
                indexDashBoardService.findFavoriteIndexPerformance(periodType);
        return ResponseEntity.ok(response);
    }
}
