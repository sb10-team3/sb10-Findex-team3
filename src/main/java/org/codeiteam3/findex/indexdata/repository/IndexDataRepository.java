package org.codeiteam3.findex.indexdata.repository;

import org.codeiteam3.findex.indexdata.entity.IndexData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IndexDataRepository extends JpaRepository<IndexData, UUID>, IndexDataRepositoryQueryDsl {
    boolean existsByIndexInfoIdAndBaseDate(UUID indexInfoId, LocalDate baseDate);

    boolean existsByIndexInfoIndexNameAndIndexInfoIndexClassificationAndBaseDate(
            String indexName,
            String indexClassification,
            LocalDate baseDate
    );

    // 전체 지수 조회용
    List<IndexData> findAllByBaseDateBetween(LocalDate startDate, LocalDate endDate, Sort sort);

    // 특정 지수 조회용
    List<IndexData> findAllByIndexInfoIdAndBaseDateBetween(UUID indexInfoId, LocalDate startDate, LocalDate endDate, Sort sort);


    // 특정 기간 내의 지수 데이터 조회
    @Query("SELECT d FROM IndexData d " +
            "JOIN FETCH d.indexInfo i " +
            "WHERE i.id = :indexInfoId " +
            "AND d.baseDate BETWEEN :startDate AND :endDate " +
            "ORDER BY d.baseDate DESC")
    List<IndexData> findChartDataByPeriod(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    // 특정 지수 조회 시
    // 특정 지수를 기준일로 이하로 내림차순으로 정렬했을때 가장 최신의 지수 데이터를 조회
    @EntityGraph(attributePaths = "indexInfo")
    List<IndexData> findTop1ByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(UUID indexInfoId, LocalDate targetDate);

    // 전체 지수 조회 시
    // 각 지수별로 타겟 날짜 이전의 가장 최신 날짜를 찾은 뒤, 원본 테이블과 조인해서 데이터를 가져옴
    @Query("SELECT d FROM IndexData d " +
            "JOIN FETCH d.indexInfo " +
            "WHERE d.baseDate = (" +
            "    SELECT MAX(sub.baseDate) FROM IndexData sub " +
            "    WHERE sub.indexInfo = d.indexInfo AND sub.baseDate <= :targetDate" +
            ")")
    List<IndexData> findLatestDataOfAllIndexesOnOrBefore(@Param("targetDate") LocalDate targetDate);

    // 즐겨찾기 되어있는 지수 조회
    @Query("SELECT d FROM IndexData d " +
            "JOIN FETCH d.indexInfo i " +
            "WHERE i.favorite = true " +
            "AND d.baseDate = ( " +
            "     SELECT MAX(sub.baseDate) FROM IndexData sub " +
            "     WHERE sub.indexInfo = d.indexInfo AND sub.baseDate <= :targetDate" +
            ")")
    List<IndexData> findFavoriteDataOnOrBefore(@Param("targetDate") LocalDate targetDate);

    // 전체 데이터 중 가장 최신 날짜 조회
    @Query("SELECT MAX(d.baseDate) FROM IndexData d")
    Optional<LocalDate> findLatestBaseDate();

    // 특정 지수의 가장 최신 날짜 조회
    @Query("SELECT MAX(d.baseDate) FROM IndexData d WHERE d.indexInfo.id = :indexInfoId")
    Optional<LocalDate> findLatestBaseDateByIndexInfoId(@Param("indexInfoId") UUID indexInfoId);
}
