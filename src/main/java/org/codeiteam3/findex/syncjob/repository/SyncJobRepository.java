package org.codeiteam3.findex.syncjob.repository;

import org.codeiteam3.findex.syncjob.entity.SyncJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
}
