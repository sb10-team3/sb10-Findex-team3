package org.codeiteam3.findex.sync_job.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record IndexDataSyncRequestDto(
        List<UUID> indexInfoIds,
        LocalDate baseDateFrom,
        LocalDate baseDateTo
) {
}
