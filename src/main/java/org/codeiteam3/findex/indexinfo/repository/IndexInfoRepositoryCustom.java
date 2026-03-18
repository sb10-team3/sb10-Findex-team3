package org.codeiteam3.findex.indexinfo.repository;

import org.codeiteam3.findex.indexinfo.entity.IndexInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface IndexInfoRepositoryCustom {

    Long countElements(
            String indexClassification,
            String indexName,
            Boolean favorite
    );

    Slice<IndexInfo> findAllByString(
            String indexClassification,
            String indexName,
            Boolean favorite,
            UUID idAfter,
            String cursor,
            String sortField,
            Sort.Direction normalizedDirection,
            Pageable pageable
    );

    Slice<IndexInfo> findAllByInteger(
            String indexClassification,
            String indexName,
            Boolean favorite,
            UUID idAfter,
            Integer cursor,
            Sort.Direction normalizedDirection,
            Pageable pageable
    );
}
