package org.codeiteam3.findex.indexdata.repository;

import org.codeiteam3.findex.indexdata.entity.IndexData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IndexDataRepository extends JpaRepository<IndexData, UUID> {
    boolean existsByIndexInfoIdAndBaseDate(UUID indexInfoId, LocalDate baseDate);

    @Query("SELECT COUNT(i) FROM IndexData AS i " +
            "WHERE (:indexInfoId IS NULL OR i.indexInfo.id = :indexInfoId) " +
            "   AND (:startDate IS NULL OR i.baseDate >= :startDate) " +
            "   AND (:endDate IS NULL OR i.baseDate <= :endDate)")
    Long countElements(@Param("indexInfoId") UUID indexInfoId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 정렬 기준이 LocalDate일 때
    @Query("SELECT i FROM IndexData AS i " +
            "WHERE (:indexInfoId IS NULL OR i.indexInfo.id = :indexInfoId) " +
            "   AND (:startDate IS NULL OR i.baseDate >= :startDate) " +
            "   AND (:endDate IS NULL OR i.baseDate <= :endDate) " +
            "   AND (:cursor IS NULL OR i.baseDate < :cursor OR (i.baseDate = :cursor AND i.id < :idAfter))")
    Slice<IndexData> findAllByBaseDateCursorDesc(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("idAfter") UUID idAfter,
            @Param("cursor") LocalDate normalizedCursor,
            Pageable pageable
    );
    @Query("SELECT i FROM IndexData AS i " +
            "WHERE (:indexInfoId IS NULL OR i.indexInfo.id = :indexInfoId) " +
            "   AND (:startDate IS NULL OR i.baseDate >= :startDate) " +
            "   AND (:endDate IS NULL OR i.baseDate <= :endDate) " +
            "   AND (:cursor IS NULL OR i.baseDate > :cursor OR (i.baseDate = :cursor AND i.id > :idAfter))")
    Slice<IndexData> findAllByBaseDateCursorAsc(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("idAfter") UUID idAfter,
            @Param("cursor") LocalDate normalizedCursor,
            Pageable pageable
    );

    // 정렬 기준이 Long일 때
    @Query("SELECT i FROM IndexData AS i " +
            "WHERE (:indexInfoId IS NULL OR i.indexInfo.id = :indexInfoId) " +
            "   AND (:startDate IS NULL OR i.baseDate >= :startDate) " +
            "   AND (:endDate IS NULL OR i.baseDate <= :endDate) " +
            "   AND (:cursor IS NULL " +
            "OR CASE " +
            "   WHEN :sortField = 'tradingQuantity' THEN i.tradingQuantity " +
            "   WHEN :sortField = 'tradingPrice' THEN i.tradingPrice " +
            "   WHEN :sortField = 'marketTotalAmount' THEN i.marketTotalAmount " +
            "END < :cursor " +
            "   OR (CASE " +
            "       WHEN :sortField = 'tradingQuantity' THEN i.tradingQuantity " +
            "       WHEN :sortField = 'tradingPrice' THEN i.tradingPrice " +
            "       WHEN :sortField = 'marketTotalAmount' THEN i.marketTotalAmount " +
            "   END = :cursor AND i.id < :isAfter))")
    Slice<IndexData> findAllByLongCursorDesc(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("isAfter") UUID isAfter,
            @Param("cursor") Long normalizedCursor,
            @Param("sortField") String normalizedSortField,
            Pageable pageable
    );
    @Query("SELECT i FROM IndexData AS i " +
            "WHERE (:indexInfoId IS NULL OR i.indexInfo.id = :indexInfoId) " +
            "   AND (:startDate IS NULL OR i.baseDate >= :startDate) " +
            "   AND (:endDate IS NULL OR i.baseDate <= :endDate) " +
            "   AND (:cursor IS NULL " +
            "OR CASE " +
            "   WHEN :sortField = 'tradingQuantity' THEN i.tradingQuantity " +
            "   WHEN :sortField = 'tradingPrice' THEN i.tradingPrice " +
            "   WHEN :sortField = 'marketTotalAmount' THEN i.marketTotalAmount " +
            "END > :cursor " +
            "   OR (CASE " +
            "       WHEN :sortField = 'tradingQuantity' THEN i.tradingQuantity " +
            "       WHEN :sortField = 'tradingPrice' THEN i.tradingPrice " +
            "       WHEN :sortField = 'marketTotalAmount' THEN i.marketTotalAmount " +
            "   END = :cursor AND i.id > :isAfter))")
    Slice<IndexData> findAllByLongCursorAsc(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("isAfter") UUID isAfter,
            @Param("cursor") Long normalizedCursor,
            @Param("sortField") String normalizedSortField,
            Pageable pageable
    );




    // 대쉬보드 메서드
    List<IndexData> findByIndexInfoIdOrderByBaseDate(UUID indexInfoId);

    UUID id(UUID id);
}
