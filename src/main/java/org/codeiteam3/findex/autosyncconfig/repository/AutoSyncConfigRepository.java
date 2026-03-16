package org.codeiteam3.findex.autosyncconfig.repository;

import org.codeiteam3.findex.autosyncconfig.AutoSyncConfig;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AutoSyncConfigRepository extends JpaRepository<AutoSyncConfig,UUID> {
    AutoSyncConfig findByIndexInfoId(UUID indexInfoId);
    @Query("SELECT a FROM AutoSyncConfig a JOIN FETCH a.indexInfo WHERE a.id = :id")
    Optional<AutoSyncConfig> findByIdWithIndexInfo(UUID id);

    @Query("""
    SELECT a
    FROM AutoSyncConfig a
    JOIN FETCH a.indexInfo
    WHERE a.enabled = true
""")
    List<AutoSyncConfig> findEnabledWithIndexInfo();

    @Query("""
SELECT a
FROM AutoSyncConfig a
WHERE (:indexInfoId IS NULL OR a.indexInfo.id = :indexInfoId)
AND (:enabled IS NULL OR a.enabled = :enabled)
AND (
        a.enabled < :cursor
        OR (a.enabled = :cursor AND a.id < :idAfter)
)
""")
    Slice<AutoSyncConfig> findAllByEnabledNextPageDesc(
            UUID indexInfoId,
            Boolean enabled,
            UUID idAfter,
            Boolean cursor,
            Pageable pageable
    );

    @Query("""
SELECT a
FROM AutoSyncConfig a
WHERE (:indexInfoId IS NULL OR a.indexInfo.id = :indexInfoId)
AND (:enabled IS NULL OR a.enabled = :enabled)
AND (
        a.enabled > :cursor
        OR (a.enabled = :cursor AND a.id > :idAfter)
)
""")
    Slice<AutoSyncConfig> findAllByEnabledNextPageAsc(
            UUID indexInfoId,
            Boolean enabled,
            UUID idAfter,
            Boolean cursor,
            Pageable pageable
    );

    @Query("""
SELECT COUNT(a)
FROM AutoSyncConfig a
WHERE (:indexInfoId IS NULL OR a.indexInfo.id = :indexInfoId)
AND (:enabled IS NULL OR a.enabled = :enabled)
""")
    Long countElements(
            UUID indexInfoId,
            Boolean enabled
    );

    @Query("""
SELECT a
FROM AutoSyncConfig a
WHERE (:indexInfoId IS NULL OR a.indexInfo.id = :indexInfoId)
AND (:enabled IS NULL OR a.enabled = :enabled)
""")
    Slice<AutoSyncConfig> findAllByIndexInfoFirstPage(
            UUID indexInfoId,
            Boolean enabled,
            Pageable pageable
    );

    @Query("""
SELECT a
FROM AutoSyncConfig a
WHERE (:indexInfoId IS NULL OR a.indexInfo.id = :indexInfoId)
AND (:enabled IS NULL OR a.enabled = :enabled)
AND (
        a.indexInfo.id < :cursor
        OR (a.indexInfo.id = :cursor AND a.id < :idAfter)
)
""")
    Slice<AutoSyncConfig> findAllByIndexInfoNextPageDesc(
            UUID indexInfoId,
            Boolean enabled,
            UUID idAfter,
            UUID cursor,
            Pageable pageable
    );

    @Query("""
SELECT a
FROM AutoSyncConfig a
WHERE (:indexInfoId IS NULL OR a.indexInfo.id = :indexInfoId)
AND (:enabled IS NULL OR a.enabled = :enabled)
AND (
        a.indexInfo.id > :cursor
        OR (a.indexInfo.id = :cursor AND a.id > :idAfter)
)
""")
    Slice<AutoSyncConfig> findAllByIndexInfoNextPageAsc(
            UUID indexInfoId,
            Boolean enabled,
            UUID idAfter,
            UUID cursor,
            Pageable pageable
    );

    @Query("""
SELECT a
FROM AutoSyncConfig a
WHERE (:indexInfoId IS NULL OR a.indexInfo.id = :indexInfoId)
AND (:enabled IS NULL OR a.enabled = :enabled)
""")
    Slice<AutoSyncConfig> findAllByEnabledFirstPage(
            UUID indexInfoId,
            Boolean enabled,
            Pageable pageable
    );

}
