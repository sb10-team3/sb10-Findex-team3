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

    @Query("""
SELECT COUNT(i)
FROM SyncJob i
WHERE (:indexInfoId IS NULL OR i.indexInfo.id = :indexInfoId)
AND i.jobTime >= COALESCE(:jobTimeFrom, i.jobTime)
AND i.jobTime <= COALESCE(:jobTimeTo, i.jobTime)
AND (:jobType IS NULL OR i.jobType = :jobType)
AND (:worker IS NULL OR i.worker = :worker)
AND (:result IS NULL OR i.result = :result)
""")
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
JOIN FETCH s.indexInfo
WHERE (:indexInfoId IS NULL OR s.indexInfo.id = :indexInfoId)
AND (:jobType IS NULL OR s.jobType = :jobType)
AND (:worker IS NULL OR s.worker = :worker)
AND (:status IS NULL OR s.result = :status)
AND (s.jobTime >= :jobTimeFrom)
AND (s.jobTime <= :jobTimeTo)
""")
    Slice<SyncJob> findAllByJobTimeFirstPage(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("jobTimeFrom") LocalDate jobTimeFrom,
            @Param("jobTimeTo") LocalDate jobTimeTo,
            @Param("jobType") JobType jobType,
            @Param("worker") String worker,
            @Param("status") Result status,
            Pageable pageable
    );

    @Query("""
SELECT s
FROM SyncJob s
JOIN FETCH s.indexInfo
WHERE (:indexInfoId IS NULL OR s.indexInfo.id = :indexInfoId)
AND (:jobType IS NULL OR s.jobType = :jobType)
AND (:worker IS NULL OR s.worker = :worker)
AND (:status IS NULL OR s.result = :status)
AND (s.jobTime >= :jobTimeFrom)
AND (s.jobTime <= :jobTimeTo)
AND (
        s.jobTime < :cursorJobTime
        OR (s.jobTime = :cursorJobTime AND s.id < :idAfter)
)
""")
    Slice<SyncJob> findAllByJobTimeNextPageDesc(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("jobTimeFrom") LocalDate jobTimeFrom,
            @Param("jobTimeTo") LocalDate jobTimeTo,
            @Param("jobType") JobType jobType,
            @Param("worker") String worker,
            @Param("status") Result status,
            @Param("idAfter") UUID idAfter,
            @Param("cursorJobTime") LocalDate cursorJobTime,
            Pageable pageable
    );

    @Query("""
SELECT s
FROM SyncJob s
JOIN FETCH s.indexInfo
WHERE (:indexInfoId IS NULL OR s.indexInfo.id = :indexInfoId)
AND (:jobType IS NULL OR s.jobType = :jobType)
AND (:worker IS NULL OR s.worker = :worker)
AND (:status IS NULL OR s.result = :status)
AND (s.jobTime >= :jobTimeFrom)
AND (s.jobTime <= :jobTimeTo)
AND (
        s.jobTime > :cursorJobTime
        OR (s.jobTime = :cursorJobTime AND s.id > :idAfter)
)
""")
    Slice<SyncJob> findAllByJobTimeNextPageAsc(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("jobTimeFrom") LocalDate jobTimeFrom,
            @Param("jobTimeTo") LocalDate jobTimeTo,
            @Param("jobType") JobType jobType,
            @Param("worker") String worker,
            @Param("status") Result status,
            @Param("idAfter") UUID idAfter,
            @Param("cursorJobTime") LocalDate cursorJobTime,
            Pageable pageable
    );

    @Query("""
SELECT s
FROM SyncJob s
JOIN FETCH s.indexInfo
WHERE (:indexInfoId IS NULL OR s.indexInfo.id = :indexInfoId)
AND (:jobType IS NULL OR s.jobType = :jobType)
AND (:worker IS NULL OR s.worker = :worker)
AND (:status IS NULL OR s.result = :status)
""")
    Slice<SyncJob> findAllByJobTimeFirstPageWithoutJobTime(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("jobType") JobType jobType,
            @Param("worker") String worker,
            @Param("status") Result status,
            Pageable pageable
    );

    @Query("""
SELECT s
FROM SyncJob s
JOIN FETCH s.indexInfo
WHERE (:indexInfoId IS NULL OR s.indexInfo.id = :indexInfoId)
AND (:jobType IS NULL OR s.jobType = :jobType)
AND (:worker IS NULL OR s.worker = :worker)
AND (:status IS NULL OR s.result = :status)
AND (
        s.jobTime < :cursorJobTime
        OR (s.jobTime = :cursorJobTime AND s.id < :idAfter)
)
""")
    Slice<SyncJob> findAllByJobTimeNextPageDescWithoutJobTime(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("jobType") JobType jobType,
            @Param("worker") String worker,
            @Param("status") Result status,
            @Param("idAfter") UUID idAfter,
            @Param("cursorJobTime") LocalDate cursorJobTime,
            Pageable pageable
    );

    @Query("""
SELECT s
FROM SyncJob s
JOIN FETCH s.indexInfo
WHERE (:indexInfoId IS NULL OR s.indexInfo.id = :indexInfoId)
AND (:jobType IS NULL OR s.jobType = :jobType)
AND (:worker IS NULL OR s.worker = :worker)
AND (:status IS NULL OR s.result = :status)
AND (
        s.jobTime > :cursorJobTime
        OR (s.jobTime = :cursorJobTime AND s.id > :idAfter)
)
""")
    Slice<SyncJob> findAllByJobTimeNextPageAscWithoutJobTime(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("jobType") JobType jobType,
            @Param("worker") String worker,
            @Param("status") Result status,
            @Param("idAfter") UUID idAfter,
            @Param("cursorJobTime") LocalDate cursorJobTime,
            Pageable pageable
    );

    //targetDate
    @Query("""
SELECT s
FROM SyncJob s
JOIN FETCH s.indexInfo
WHERE (:indexInfoId IS NULL OR s.indexInfo.id = :indexInfoId)
AND (:jobType IS NULL OR s.jobType = :jobType)
AND (:worker IS NULL OR s.worker = :worker)
AND (:status IS NULL OR s.result = :status)
AND (s.jobTime >= :jobTimeFrom)
AND (s.jobTime <= :jobTimeTo)
""")
    Slice<SyncJob> findAllByTargetDateFirstPage(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("jobTimeFrom") LocalDate jobTimeFrom,
            @Param("jobTimeTo") LocalDate jobTimeTo,
            @Param("jobType") JobType jobType,
            @Param("worker") String worker,
            @Param("status") Result status,
            Pageable pageable
    );

    @Query("""
SELECT s
FROM SyncJob s
JOIN FETCH s.indexInfo
WHERE (:indexInfoId IS NULL OR s.indexInfo.id = :indexInfoId)
AND (:jobType IS NULL OR s.jobType = :jobType)
AND (:worker IS NULL OR s.worker = :worker)
AND (:status IS NULL OR s.result = :status)
AND (s.jobTime >= :jobTimeFrom)
AND (s.jobTime <= :jobTimeTo)
AND (
        s.targetDate < :cursorTargetDate
        OR (s.targetDate = :cursorTargetDate AND s.id < :idAfter)
)
""")
    Slice<SyncJob> findAllByTargetDateNextPageDesc(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("jobTimeFrom") LocalDate jobTimeFrom,
            @Param("jobTimeTo") LocalDate jobTimeTo,
            @Param("jobType") JobType jobType,
            @Param("worker") String worker,
            @Param("status") Result status,
            @Param("idAfter") UUID idAfter,
            @Param("cursorTargetDate") LocalDate cursorTargetDate,
            Pageable pageable
    );

    @Query("""
SELECT s
FROM SyncJob s
JOIN FETCH s.indexInfo
WHERE (:indexInfoId IS NULL OR s.indexInfo.id = :indexInfoId)
AND (:jobType IS NULL OR s.jobType = :jobType)
AND (:worker IS NULL OR s.worker = :worker)
AND (:status IS NULL OR s.result = :status)
AND (s.jobTime >= :jobTimeFrom)
AND (s.jobTime <= :jobTimeTo)
AND (
        s.targetDate > :cursorTargetDate
        OR (s.targetDate = :cursorTargetDate AND s.id > :idAfter)
)
""")
    Slice<SyncJob> findAllByTargetDateNextPageAsc(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("jobTimeFrom") LocalDate jobTimeFrom,
            @Param("jobTimeTo") LocalDate jobTimeTo,
            @Param("jobType") JobType jobType,
            @Param("worker") String worker,
            @Param("status") Result status,
            @Param("idAfter") UUID idAfter,
            @Param("cursorTargetDate") LocalDate cursorTargetDate,
            Pageable pageable
    );

    //날짜 조건 X
    @Query("""
SELECT s
FROM SyncJob s
JOIN FETCH s.indexInfo
WHERE (:indexInfoId IS NULL OR s.indexInfo.id = :indexInfoId)
AND (:jobType IS NULL OR s.jobType = :jobType)
AND (:worker IS NULL OR s.worker = :worker)
AND (:status IS NULL OR s.result = :status)
""")
    Slice<SyncJob> findAllByTargetDateFirstPageWithoutJobTime(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("jobType") JobType jobType,
            @Param("worker") String worker,
            @Param("status") Result status,
            Pageable pageable
    );

    @Query("""
SELECT s
FROM SyncJob s
JOIN FETCH s.indexInfo
WHERE (:indexInfoId IS NULL OR s.indexInfo.id = :indexInfoId)
AND (:jobType IS NULL OR s.jobType = :jobType)
AND (:worker IS NULL OR s.worker = :worker)
AND (:status IS NULL OR s.result = :status)
AND (
        s.targetDate < :cursorTargetDate
        OR (s.targetDate = :cursorTargetDate AND s.id < :idAfter)
)
""")
    Slice<SyncJob> findAllByTargetDateNextPageDescWithoutJobTime(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("jobType") JobType jobType,
            @Param("worker") String worker,
            @Param("status") Result status,
            @Param("idAfter") UUID idAfter,
            @Param("cursorTargetDate") LocalDate cursorTargetDate,
            Pageable pageable
    );

    @Query("""
SELECT s
FROM SyncJob s
JOIN FETCH s.indexInfo
WHERE (:indexInfoId IS NULL OR s.indexInfo.id = :indexInfoId)
AND (:jobType IS NULL OR s.jobType = :jobType)
AND (:worker IS NULL OR s.worker = :worker)
AND (:status IS NULL OR s.result = :status)
AND (
        s.targetDate > :cursorTargetDate
        OR (s.targetDate = :cursorTargetDate AND s.id > :idAfter)
)
""")
    Slice<SyncJob> findAllByTargetDateNextPageAscWithoutJobTime(
            @Param("indexInfoId") UUID indexInfoId,
            @Param("jobType") JobType jobType,
            @Param("worker") String worker,
            @Param("status") Result status,
            @Param("idAfter") UUID idAfter,
            @Param("cursorTargetDate") LocalDate cursorTargetDate,
            Pageable pageable
    );
}