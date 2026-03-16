package org.codeiteam3.findex.autosyncconfig.repository;

import org.codeiteam3.findex.autosyncconfig.AutoSyncConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AutoSyncConfigRepository extends JpaRepository<AutoSyncConfig,UUID> {
    AutoSyncConfig findByIndexInfoId(UUID indexInfoId);
}
