package org.codeiteam3.findex.autosyncconfig.repository;

import org.codeiteam3.findex.autosyncconfig.AutoSyncConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface AutoSyncConfigRepository extends JpaRepository<AutoSyncConfig,UUID> {
    AutoSyncConfig findByIndexInfoId(UUID indexInfoId);
    @Query("SELECT a FROM AutoSyncConfig a JOIN FETCH a.indexInfo WHERE a.id = :id")
    Optional<AutoSyncConfig> findByIdWithIndexInfo(UUID id);
}
