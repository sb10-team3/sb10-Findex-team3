package org.codeiteam3.findex.indexdata.repository;

import org.codeiteam3.findex.indexdata.entity.IndexData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IndexDataRepository extends JpaRepository<IndexData, UUID> {
    // CRUD 메서드


    List<IndexData> findByIndexInfoIdOrderByBaseDate(UUID indexInfoId);
}
