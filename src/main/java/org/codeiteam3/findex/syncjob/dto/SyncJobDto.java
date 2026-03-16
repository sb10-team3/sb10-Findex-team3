package org.codeiteam3.findex.syncjob.dto;

import org.codeiteam3.findex.enums.JobType;
import org.codeiteam3.findex.enums.Result;

import java.time.LocalDate;
import java.util.UUID;

public record SyncJobDto(
        UUID id,
        JobType jobType,
        UUID indexInfoId,
        LocalDate targetDate,
        String worker,
        LocalDate jobTime,
        Result result
) {
}
