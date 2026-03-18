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

    // 특정 지수 조회 시
    // 특정 지수를 기준일로 이하로 내림차순으로 정렬했을때 가장 최신의 지수 데이터를 조회
    @EntityGraph(attributePaths = "indexInfo")
    List<IndexData> findTop1ByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(UUID indexInfoId, LocalDate targetDate);


    // 전체 데이터 중 가장 최신 날짜 조회
    @Query("SELECT MAX(d.baseDate) FROM IndexData d")
    Optional<LocalDate> findLatestBaseDate();
}
