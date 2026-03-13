package org.codeiteam3.findex.indexinfo.repository;

import org.codeiteam3.findex.indexinfo.entity.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, UUID> {

}
