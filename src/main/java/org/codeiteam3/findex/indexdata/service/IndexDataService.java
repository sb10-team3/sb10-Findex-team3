package org.codeiteam3.findex.indexdata.service;

import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.common.CursorPageResponse;
import org.codeiteam3.findex.common.CursorPageResponseMapper;
import org.codeiteam3.findex.enums.SourceType;
import org.codeiteam3.findex.indexdata.dto.CursorPageResponseIndexDataDto;
import org.codeiteam3.findex.indexdata.dto.IndexDataCreateRequest;
import org.codeiteam3.findex.indexdata.dto.IndexDataDto;
import org.codeiteam3.findex.indexdata.entity.IndexData;
import org.codeiteam3.findex.indexdata.mapper.IndexDataMapper;
import org.codeiteam3.findex.indexdata.repository.IndexDataRepository;
import org.codeiteam3.findex.indexinfo.entity.IndexInfo;
import org.codeiteam3.findex.indexinfo.repository.IndexInfoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class IndexDataService {
    private final IndexDataRepository indexDataRepository;
    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataMapper indexDataMapper;
    private final CursorPageResponseMapper cursorPageResponseMapper;

    // 지수 데이터 등록
    public IndexDataDto create(IndexDataCreateRequest request) {
        UUID indexInfoId = request.indexInfoId();

        // 지수 데이터 존재 여부
        if (indexDataRepository.existsByIndexInfoIdAndBaseDate(indexInfoId, request.baseDate())) {
            throw new IllegalArgumentException("같은 지수와 날짜가 데이터에 이미 존재합니다.");
        }

        BigDecimal lowPrice = request.lowPrice();
        BigDecimal highPrice = request.highPrice();
        BigDecimal marketPrice = request.marketPrice();
        BigDecimal closingPrice = request.closingPrice();

        // `lowPrice` <= `highPrice`
        if (lowPrice.compareTo(highPrice) > 0) {
            throw new IllegalArgumentException("고가(highPrice)는 저가(lowPrice)보다 작을 수 없습니다.");
        }
        // `lowPrice` <= `marketPrice <= `highPrice`
        if ((lowPrice.compareTo(marketPrice) > 0) || (highPrice.compareTo(marketPrice) < 0)) {
            throw new IllegalArgumentException("시가(marketPrice)는 저가(lowPrice)보다 크거나 같고, 고가(highPrice)보다 작거나 같아야 합니다.");
        }
        // `lowPrice` <= `closingPrice <= `highPrice`
        if ((lowPrice.compareTo(closingPrice) > 0) || (highPrice.compareTo(closingPrice) < 0)) {
            throw new IllegalArgumentException("종가(closingPrice)는 저가(lowPrice)보다 크거나 같고, 고가(highPrice)보다 작거나 같아야 합니다.");
        }

        // 지수 데이터 생성
        IndexInfo indexInfo = indexInfoRepository.findById(request.indexInfoId())
                .orElseThrow(() ->new NoSuchElementException(indexInfoId + "를 가진 IndexInfo를 찾을 수 없습니다."));

        IndexData indexData = new IndexData(
                indexInfo,
                request.baseDate(),
                SourceType.USER,
                request.marketPrice(),
                request.closingPrice(),
                request.highPrice(),
                request.lowPrice(),
                request.versus(),
                request.fluctuationRate(),
                request.tradingQuantity(),
                request.tradingPrice(),
                request.marketTotalAmount()
        );
        indexDataRepository.save(indexData);

        return indexDataMapper.toDto(indexData);
    }

    // 지수 데이터 조회
    public CursorPageResponseIndexDataDto findAll(UUID indexInfoId, LocalDate startDate, LocalDate endDate, UUID idAfter, String cursor, String sortField, String sortDirection, int size) {
        // validate
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(" 종료 일자(endDate)는 시작 일자(startDate)보다 앞설 수 없습니다.");
        }
        if (size < 1) {
            throw new IllegalArgumentException(" 페이지 크기(size)는 1 이상이어야 합니다.");
        }

        // 지수 데이터 존재 확인
        if (indexInfoId != null) {
            indexInfoRepository.findById(indexInfoId)
                    .orElseThrow(() ->new NoSuchElementException(indexInfoId + "를 가진 IndexInfo를 찾을 수 없습니다."));
        }

        // cursor
        String normalizedCursor  = (cursor == null || cursor.isBlank()) ? null : cursor;

        // sortField
        Set<String> allowField = Set.of("baseDate", "marketPrice", "closingPrice", "highPrice", "lowPrice", "versus", "fluctuationRate", "tradingQuantity", "tradingPrice", "marketTotalAmount");
        if (!allowField.contains(sortField)) {
            throw new IllegalArgumentException("적합하지 않은 정렬필드(sortField)입니다.");
        }
        String normalizedSortField = sortField;

        // 정렬
        Sort.Direction normalizedDirection = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Pageable (cursor, sortField, 정렬, 갯수 적용)
        Pageable pageable = PageRequest.of(0, size, Sort.by(normalizedDirection, normalizedSortField).and(Sort.by(normalizedDirection, "id")));

        // 조회 조건에 따라 계산된 전체 데이터 수
        Long totalElements = indexDataRepository.countElements(indexInfoId, startDate, endDate);

        // 지수 데이터 조회
        Slice<IndexDataDto> indexDataSlice = findIndexDataSlice(indexInfoId, startDate, endDate, idAfter, normalizedCursor, normalizedSortField, normalizedDirection, pageable)
                .map(indexData -> indexDataMapper.toDto(indexData));
        // 조회 데이터 중 마지막 요소
        IndexDataDto lastIndexData = indexDataSlice.getContent().get(indexDataSlice.getNumberOfElements() - 1);

        String nextCursor = null; // sortField에 따라 달라짐
        UUID nextIdAfter = null;
        if (indexDataSlice.hasNext()) {
            nextCursor = findNextCursor(lastIndexData, normalizedSortField); // 다음 페이지 커서
            nextIdAfter = lastIndexData.id(); // 마지막 요소 ID
        }

        CursorPageResponse<IndexDataDto> cursorPageResponse = cursorPageResponseMapper.fromSlice(
                indexDataSlice,
                nextCursor,
                nextIdAfter,
                totalElements
        );

        return new CursorPageResponseIndexDataDto(cursorPageResponse);
    }

    private Slice<IndexData> findIndexDataSlice(
            UUID indexInfoId,
            LocalDate startDate,
            LocalDate endDate,
            UUID idAfter,
            String normalizedCursor,
            String normalizedSortField,
            Sort.Direction normalizedDirection,
            Pageable pageable
    ) {
        // switch 문으로 sortField의 타입에 따라 다른 repo 메서드 사용
        return switch (normalizedSortField) {
            case "baseDate" -> normalizedDirection.isDescending()
                    ? indexDataRepository.findAllByBaseDateCursorDesc(indexInfoId, startDate, endDate, idAfter, parseLocalDateCursor(normalizedCursor), pageable)
                    : indexDataRepository.findAllByBaseDateCursorAsc(indexInfoId, startDate, endDate, idAfter, parseLocalDateCursor(normalizedCursor), pageable);
            case "marketPrice", "closingPrice", "highPrice", "lowPrice", "versus", "fluctuationRate" -> normalizedDirection.isDescending()
                    ? indexDataRepository.findAllByBigDecimalCursorDesc(indexInfoId, startDate, endDate, idAfter, parseBigDecimalCursor(normalizedCursor), normalizedSortField, pageable)
                    : indexDataRepository.findAllByBigDecimalCursorAsc(indexInfoId, startDate, endDate, idAfter, parseBigDecimalCursor(normalizedCursor), normalizedSortField, pageable);
            case "tradingQuantity", "tradingPrice", "marketTotalAmount" -> normalizedDirection.isDescending()
                    ? indexDataRepository.findAllByLongCursorDesc(indexInfoId, startDate, endDate, idAfter, parseLongCursor(normalizedCursor), normalizedSortField, pageable)
                    : indexDataRepository.findAllByLongCursorAsc(indexInfoId, startDate, endDate, idAfter, parseLongCursor(normalizedCursor), normalizedSortField, pageable);
            default -> throw new IllegalArgumentException("제대로 되지 않은 sortField 입니다.");
        };
    }

    // String(문자열) -> 다른 타입으로 parse
    // String -> LocalDate
    private LocalDate parseLocalDateCursor(String cursor) {
        if (cursor == null) return null;
        return LocalDate.parse(cursor);
    }
    // String -> BigDecimal
    private BigDecimal parseBigDecimalCursor(String cursor) {
        if (cursor == null) return null;
        return new BigDecimal(cursor);
    }
    // String -> Long
    private Long parseLongCursor(String cursor) {
        if (cursor == null) return null;
        return Long.parseLong(cursor);
    }

    private String findNextCursor(IndexDataDto lastIndexData, String normalizedSortField) {
        return switch (normalizedSortField) {
            case "baseDate" -> lastIndexData.baseDate().toString();
            case "marketPrice" -> lastIndexData.marketPrice().toPlainString();
            case "closingPrice" -> lastIndexData.closingPrice().toPlainString();
            case "highPrice" -> lastIndexData.highPrice().toPlainString();
            case "lowPrice" -> lastIndexData.lowPrice().toPlainString();
            case "versus" -> lastIndexData.versus().toPlainString();
            case "fluctuationRate" -> lastIndexData.fluctuationRate().toPlainString();
            case "tradingQuantity" -> lastIndexData.tradingQuantity().toString();
            case "tradingPrice" -> lastIndexData.tradingPrice().toString();
            case "marketTotalAmount" -> lastIndexData.marketTotalAmount().toString();
            default -> throw new IllegalArgumentException("제대로 되지 않은 sortField 입니다.");
        };
    }
}
