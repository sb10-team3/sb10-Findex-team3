package org.codeiteam3.findex.indexdata.controller;

import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.enums.PeriodType;
import org.codeiteam3.findex.indexdata.dto.IndexChartDto;
import org.codeiteam3.findex.indexdata.service.IndexChartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/index-data")
public class IndexChartController {
    private IndexChartService indexChartService;

    @GetMapping(value = "/{id}/chart")
    public ResponseEntity<IndexChartDto> findIndexChart(@PathVariable("id") UUID indexInfoId,
                                         @RequestParam(required = false) PeriodType periodType){
        IndexChartDto response = indexChartService.find(indexInfoId, periodType);
        return ResponseEntity.ok(response);
    }
}
