package org.codeiteam3.findex.indexinfo.repository;

import org.codeiteam3.findex.enums.SourceType;
import org.codeiteam3.findex.indexinfo.entity.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, UUID> {

    boolean existsByIndexClassificationAndIndexName( //지수분류 + 지수 이름으로 존재 검증
                                                     String indexClassification,
                                                     String indexName
    );

    boolean existsByIndexClassificationAndIndexNameAndSourceType(
            String indexClassification,
            String indexName,
            SourceType sourceType
    );

    IndexInfo findByIndexClassificationAndIndexNameAndSourceType(
            String indexClassification,
            String indexName,
            SourceType sourceType
    );

    @Query("""
    SELECT COUNT(i)
    FROM IndexInfo i
    WHERE (:indexClassification IS NULL OR i.indexClassification LIKE CONCAT('%', :indexClassification, '%'))
    AND (:indexName IS NULL OR i.indexName LIKE CONCAT('%', :indexName, '%'))
    AND (:favorite IS NULL OR i.favorite = :favorite)
    """)
    Long countElements(
            @Param("indexClassification") String indexClassification,
            @Param("indexName") String indexName,
            @Param("favorite") Boolean favorite
    );

}
