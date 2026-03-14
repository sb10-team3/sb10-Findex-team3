package org.codeiteam3.findex.indexdata.repository;

import org.codeiteam3.findex.indexdata.entity.IndexData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IndexDataRepository extends JpaRepository<IndexData, UUID> {
    boolean existsByIndexInfoIdAndBaseDate(UUID indexInfoId, LocalDate baseDate);

    @Query("SELECT COUNT(i) " +
            "FROM IndexData AS i " +
            "WHERE (:indexInfoId IS NULL OR i.indexInfo.id = :indexInfoId) " +
            "AND (:startDate IS NULL OR i.baseDate >= :startDate) " +
            "AND (:endDate IS NULL OR i.baseDate <= :endDate)")
    Long countElements(@Param("indexInfoId") UUID indexInfoId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);






    // 대쉬보드 메서드
    List<IndexData> findByIndexInfoIdOrderByBaseDate(UUID indexInfoId);
}
