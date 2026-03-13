package org.codeiteam3.findex.indexinfo.controller;

import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.indexinfo.dto.data.IndexInfoDto;
import org.codeiteam3.findex.indexinfo.dto.request.IndexInfoCreateRequest;
import org.codeiteam3.findex.indexinfo.entity.IndexInfo;
import org.codeiteam3.findex.indexinfo.service.IndexInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/index-infos")
@RequiredArgsConstructor
public class IndexInfoController {
    private final IndexInfoService indexInfoService;

    @PostMapping
    public ResponseEntity<IndexInfoDto> create(@RequestBody IndexInfoCreateRequest indexInfoCreateRequest) {
        IndexInfo indexInfo = indexInfoService.create(indexInfoCreateRequest);

        IndexInfoDto indexInfoDto = new IndexInfoDto(
                indexInfo.getId(),
                indexInfo.getIndexClassification(),
                indexInfo.getIndexName(),
                indexInfo.getEmployedItemsCount(),
                indexInfo.getBasePointInTime(),
                indexInfo.getBaseIndex(),
                indexInfo.getSourceType(),
                indexInfo.getFavorite()
        );

        return ResponseEntity.ok(indexInfoDto);




    }
}
