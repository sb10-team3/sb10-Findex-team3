package org.codeiteam3.findex.indexinfo.repository;

import org.codeiteam3.findex.enums.SourceType;
import org.codeiteam3.findex.indexinfo.entity.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;

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

}
