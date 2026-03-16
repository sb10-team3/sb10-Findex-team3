package org.codeiteam3.findex.indexdata.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.exception.ErrorResponse;
import org.codeiteam3.findex.indexdata.dto.CursorPageResponseIndexDataDto;
import org.codeiteam3.findex.indexdata.dto.IndexDataCreateRequest;
import org.codeiteam3.findex.indexdata.dto.IndexDataDto;
import org.codeiteam3.findex.indexdata.dto.IndexDataUpdateRequest;
import org.codeiteam3.findex.indexdata.service.IndexDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/index-data")
@RequiredArgsConstructor
@Tag(name = "지수 데이터 API", description = "지수 데이터 관리 API")
public class IndexDataController {
    private final IndexDataService indexDataService;

    @PostMapping
    @Operation(summary = "지수 데이터 등록", description = "새로운 지수 데이터를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "지수 데이터 생성 성공", content = @Content(schema = @Schema(implementation = IndexDataDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 데이터 값 등)", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "참조하는 지수 정보를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<IndexDataDto> create(@Valid @RequestBody IndexDataCreateRequest request) {
        IndexDataDto responseDto = indexDataService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    @Operation(summary = "지수 데이터 목록 조회", description = "지수 데이터 목록을 조회합니다. 필터링, 정렬, 커서 기반 페이지네이션을 지원합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "지수 데이터 목록 조회 성공", content = @Content(schema = @Schema(implementation = CursorPageResponseIndexDataDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CursorPageResponseIndexDataDto> findAll(
            @Parameter(description = "지수 정보 ID") @RequestParam(required = false) UUID indexInfoId,
            @Parameter(description = "시작 일자") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "종료 일자") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "이전 페이지 마지막 요소 ID") @RequestParam(required = false) UUID idAfter,
            @Parameter(description = "커서 (다음 페이지 시작점") @RequestParam(required = false) String cursor,
            @Parameter(description = "정렬 필드 (baseDate, marketPrice, closingPrice, highPrice, lowPrice, versus, fluctuationRate, tradingQuantity, tradingPrice, marketTotalAmount)") @RequestParam(required = false, defaultValue = "baseDate") String sortField,
            @Parameter(description = "정렬 방향 (asc, desc)") @RequestParam(required = false, defaultValue = "desc") String sortDirection,
            @Parameter(description = "페이지 크기") @RequestParam(required = false, defaultValue = "10") int size
    ) {
        CursorPageResponseIndexDataDto responseDto = indexDataService.findAll(indexInfoId, startDate, endDate, idAfter, cursor, sortField, sortDirection, size);


        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }


    @PatchMapping(value = "/{id}")
    @Operation(summary = "지수 데이터 수정", description = "기존 지수 데이터를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "지수 데이터 수정 성공", content = @Content(schema = @Schema(implementation = IndexDataDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 데이터 값 등)", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "수정할 지수 데이터를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<IndexDataDto> update(
            @Parameter(description = "지수 데이터 ID") @PathVariable UUID id,
            @Valid @Parameter @RequestBody IndexDataUpdateRequest request
    ) {
        IndexDataDto responseDto = indexDataService.update(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "지수 데이터 삭제", description = "지수 데이터를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "지수 데이터 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "삭제할 지수 데이터를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(@Parameter(description = "지수 데이터 ID") @PathVariable UUID id) {
        indexDataService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
