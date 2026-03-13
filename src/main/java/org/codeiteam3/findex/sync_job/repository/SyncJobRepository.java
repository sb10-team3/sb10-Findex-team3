package org.codeiteam3.findex.sync_job.repository;

import org.codeiteam3.findex.sync_job.SyncJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SyncJobRepository extends JpaRepository<SyncJob, UUID> {
}
