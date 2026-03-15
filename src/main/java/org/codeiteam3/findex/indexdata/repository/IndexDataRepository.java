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










    List<IndexData> findByIndexInfoIdOrderByBaseDate(UUID indexInfoId);
    // 특정 지수 조회 시
    // 특정 지수를 기준일로 이하로 내림차순으로 정렬했을때 가장 최신의 지수 데이터를 조회
    List<IndexData> findTop1ByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(UUID indexInfoId, LocalDate baseDateIsLessThan);
    // 전체 지수 조회 시
    // 각 지수별로 타겟 날짜 이전의 가장 최신 날짜를 찾은 뒤, 원본 테이블과 조인해서 데이터를 가져옴
    @Query(value =
            "SELECT d.* FROM index_datas d " +
                    "INNER JOIN (" +
                    "    SELECT index_info_id, MAX(base_date) AS max_date " +
                    "    FROM index_datas " +
                    "    WHERE base_date <= :targetDate " +
                    "    GROUP BY index_info_id" +
                    ") sub ON d.index_info_id = sub.index_info_id AND d.base_date = sub.max_date",
            nativeQuery = true)
    List<IndexData> findLatestDataOfAllIndexesOnOrBefore(@Param("targetDate") LocalDate targetDate);
}
