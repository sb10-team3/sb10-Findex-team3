package org.codeiteam3.findex.indexdata.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.indexdata.dto.IndexDataCreateRequest;
import org.codeiteam3.findex.indexdata.dto.IndexDataDto;
import org.codeiteam3.findex.indexdata.service.IndexDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/index-data")
@RequiredArgsConstructor
public class IndexDataController {
    private final IndexDataService indexDataService;

    @PostMapping
    public ResponseEntity<IndexDataDto> create(@Valid @RequestBody IndexDataCreateRequest request) {
        IndexDataDto indexDataDto = indexDataService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(indexDataDto);
    }

}
