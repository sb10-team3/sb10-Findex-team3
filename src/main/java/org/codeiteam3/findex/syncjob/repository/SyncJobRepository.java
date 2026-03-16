package org.codeiteam3.findex.syncjob.repository;

import org.codeiteam3.findex.enums.JobType;
import org.codeiteam3.findex.enums.Result;
import org.codeiteam3.findex.indexdata.entity.IndexData;
import org.codeiteam3.findex.syncjob.entity.SyncJob;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SyncJobRepository extends JpaRepository<SyncJob, UUID> {

    @Query("""
select max(s.jobTime)
from SyncJob s
where s.indexInfo.id = :indexInfoId
and s.worker = :worker
""")
    Optional<LocalDate> findLastJobTime(UUID indexInfoId, String worker);

    @Query("SELECT COUNT(i) FROM SyncJob AS i " +
            "WHERE (:indexInfoId IS NULL OR i.indexInfo.id = :indexInfoId) " +
            "   AND i.jobTime >= COALESCE(:jobTimeFrom, i.jobTime) " +
            "   AND i.jobTime <= COALESCE(:jobTimeTo, i.jobTime) " +
            "   AND i.jobType = :jobType" +
            "   AND (:worker IS NULL OR i.worker = :worker) " +
            "   AND (:result IS NULL OR i.result = :result) "
    )
    Long countElements(@Param("indexInfoId") UUID indexInfoId,
                       @Param("jobTimeFrom") LocalDate jobTimeFrom,
                       @Param("jobTimeTo") LocalDate jobTimeTo,
                       @Param("jobType") JobType jobType,
                       @Param("worker") String worker,
                       @Param("result") Result result
    );

    @Query("""
SELECT s
FROM SyncJob s
WHERE (:indexInfoId IS NULL OR s.indexInfo.id = :indexInfoId)
AND (:jobType IS NULL OR s.jobType = :jobType)
AND (:worker IS NULL OR s.worker = :worker)
AND (:status IS NULL OR s.result = :status)
AND (:jobTimeFrom IS NULL OR s.jobTime >= :jobTimeFrom)
AND (:jobTimeTo IS NULL OR s.jobTime <= :jobTimeTo)
""")
    Slice<SyncJob> findAllByJobTimeFirstPage(
            UUID indexInfoId,
            LocalDate jobTimeFrom,
            LocalDate jobTimeTo,
            JobType jobType,
            String worker,
            Result status,
            Pageable pageable
    );

    @Query("""
SELECT s
FROM SyncJob s
WHERE (:indexInfoId IS NULL OR s.indexInfo.id = :indexInfoId)
AND (:jobType IS NULL OR s.jobType = :jobType)
AND (:worker IS NULL OR s.worker = :worker)
AND (:status IS NULL OR s.result = :status)
AND (:jobTimeFrom IS NULL OR s.jobTime >= :jobTimeFrom)
AND (:jobTimeTo IS NULL OR s.jobTime <= :jobTimeTo)
AND (
        s.jobTime < :cursorJobTime
        OR (s.jobTime = :cursorJobTime AND s.id < :idAfter)
)
""")
    Slice<SyncJob> findAllByJobTimeNextPageDesc(
            UUID indexInfoId,
            LocalDate jobTimeFrom,
            LocalDate jobTimeTo,
            JobType jobType,
            String worker,
            Result status,
            UUID idAfter,
            LocalDate cursorJobTime,
            Pageable pageable
    );

    @Query("""
SELECT s
FROM SyncJob s
WHERE (:indexInfoId IS NULL OR s.indexInfo.id = :indexInfoId)
AND (:jobType IS NULL OR s.jobType = :jobType)
AND (:worker IS NULL OR s.worker = :worker)
AND (:status IS NULL OR s.result = :status)
AND (:jobTimeFrom IS NULL OR s.jobTime >= :jobTimeFrom)
AND (:jobTimeTo IS NULL OR s.jobTime <= :jobTimeTo)
AND (
        s.jobTime > :cursorJobTime
        OR (s.jobTime = :cursorJobTime AND s.id > :idAfter)
)
""")
    Slice<SyncJob> findAllByJobTimeNextPageAsc(
            UUID indexInfoId,
            LocalDate jobTimeFrom,
            LocalDate jobTimeTo,
            JobType jobType,
            String worker,
            Result status,
            UUID idAfter,
            LocalDate cursorJobTime,
            Pageable pageable
    );

    @Query("""
SELECT s
FROM SyncJob s
WHERE (:indexInfoId IS NULL OR s.indexInfo.id = :indexInfoId)
AND (:jobType IS NULL OR s.jobType = :jobType)
AND (:worker IS NULL OR s.worker = :worker)
AND (:status IS NULL OR s.result = :status)
AND (:jobTimeFrom IS NULL OR s.jobTime >= :jobTimeFrom)
AND (:jobTimeTo IS NULL OR s.jobTime <= :jobTimeTo)
""")
    Slice<SyncJob> findAllByTargetDateFirstPage(
            UUID indexInfoId,
            LocalDate jobTimeFrom,
            LocalDate jobTimeTo,
            JobType jobType,
            String worker,
            Result status,
            Pageable pageable
    );

    @Query("""
SELECT s
FROM SyncJob s
WHERE (:indexInfoId IS NULL OR s.indexInfo.id = :indexInfoId)
AND (:jobType IS NULL OR s.jobType = :jobType)
AND (:worker IS NULL OR s.worker = :worker)
AND (:status IS NULL OR s.result = :status)
AND (:jobTimeFrom IS NULL OR s.jobTime >= :jobTimeFrom)
AND (:jobTimeTo IS NULL OR s.jobTime <= :jobTimeTo)
AND (
        s.targetDate < :cursorTargetDate
        OR (s.targetDate = :cursorTargetDate AND s.id < :idAfter)
)
""")
    Slice<SyncJob> findAllByTargetDateNextPageDesc(
            UUID indexInfoId,
            LocalDate jobTimeFrom,
            LocalDate jobTimeTo,
            JobType jobType,
            String worker,
            Result status,
            UUID idAfter,
            LocalDate cursorTargetDate,
            Pageable pageable
    );

    @Query("""
SELECT s
FROM SyncJob s
WHERE (:indexInfoId IS NULL OR s.indexInfo.id = :indexInfoId)
AND (:jobType IS NULL OR s.jobType = :jobType)
AND (:worker IS NULL OR s.worker = :worker)
AND (:status IS NULL OR s.result = :status)
AND (:jobTimeFrom IS NULL OR s.jobTime >= :jobTimeFrom)
AND (:jobTimeTo IS NULL OR s.jobTime <= :jobTimeTo)
AND (
        s.targetDate > :cursorTargetDate
        OR (s.targetDate = :cursorTargetDate AND s.id > :idAfter)
)
""")
    Slice<SyncJob> findAllByTargetDateNextPageAsc(
            UUID indexInfoId,
            LocalDate jobTimeFrom,
            LocalDate jobTimeTo,
            JobType jobType,
            String worker,
            Result status,
            UUID idAfter,
            LocalDate cursorTargetDate,
            Pageable pageable
    );
}
