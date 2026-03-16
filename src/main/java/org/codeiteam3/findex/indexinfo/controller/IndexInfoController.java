package org.codeiteam3.findex.indexinfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.common.CursorPageResponse;
import org.codeiteam3.findex.indexinfo.dto.data.CursorPageResponseIndexInfoDto;
import org.codeiteam3.findex.indexinfo.dto.data.IndexInfoDto;
import org.codeiteam3.findex.indexinfo.dto.request.IndexInfoCreateRequest;
import org.codeiteam3.findex.indexinfo.entity.IndexInfo;
import org.codeiteam3.findex.indexinfo.mapper.IndexInfoMapper;
import org.codeiteam3.findex.indexinfo.service.IndexInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/index-infos")
@RequiredArgsConstructor
public class IndexInfoController {
    private final IndexInfoService indexInfoService;
    private final IndexInfoMapper indexInfoMapper;

    @PostMapping
    public ResponseEntity<IndexInfoDto> create(@Valid @RequestBody IndexInfoCreateRequest indexInfoCreateRequest) {
        IndexInfo indexInfo = indexInfoService.create(indexInfoCreateRequest);

        IndexInfoDto indexInfoDto = indexInfoMapper.toDto(indexInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(indexInfoDto);

    }
    @GetMapping
    @Operation(summary = "지수 정보 목록 조회",description = "지수 정보 목록을 조회합니다. 필터링,정렬,커서 기반 페이지네이션을 지원합니다.")
    public ResponseEntity<CursorPageResponseIndexInfoDto> findAll(@Parameter(description = "지수 분류명") @RequestParam(required = false) String indexClassification,
                                                                  @Parameter(description = "지수명") @RequestParam(required = false) String indexName,
                                                                  @Parameter(description = "즐겨찾기 여부") @RequestParam(required = false) Boolean favorite,
                                                                  @Parameter(description = "이전 페이지 마지막 요소 ID") @RequestParam UUID idAfter,
                                                                  @Parameter(description = "커서 (다음 페이지 시작점)") @RequestParam String cursor,
                                                                  @Parameter(description = "정렬필드(indexClassification,indexName,employedItemsCount)") @RequestParam String sortField,
                                                                  @Parameter(description = "정렬방향(asc,desc)") @RequestParam String sortDirection,
                                                                  @Parameter(description = "페이지 크기") @RequestParam Integer size

                                                                  ) {
        CursorPageResponseIndexInfoDto responseDto = indexInfoService.findAll(indexClassification,indexName,favorite,idAfter,cursor,sortField,sortDirection,size);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IndexInfoDto> find(@PathVariable("id") UUID id) {
        IndexInfo indexInfo = indexInfoService.findById(id);
        IndexInfoDto indexInfoDto = indexInfoMapper.toDto(indexInfo);
        return ResponseEntity.ok(indexInfoDto);

    }
}
