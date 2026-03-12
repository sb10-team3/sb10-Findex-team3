package org.codeiteam3.findex.sync_job.dto;

import org.codeiteam3.findex.sync_job.JobType;
import org.codeiteam3.findex.sync_job.Result;

import java.time.Instant;
import java.util.UUID;

public record SyncJobDto(
        UUID id,
        JobType jobType,
        UUID indexInfoId,
        Instant targetDate,
        String worker,
        Instant jobTime,
        Result result
) {
}
