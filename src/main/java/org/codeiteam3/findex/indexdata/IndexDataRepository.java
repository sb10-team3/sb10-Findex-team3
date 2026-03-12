package org.codeiteam3.findex.indexdata;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IndexDataRepository extends JpaRepository<IndexData, UUID> {

}
