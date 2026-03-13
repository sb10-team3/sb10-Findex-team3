package org.codeiteam3.findex.sync_job.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record IndexDataSyncRequestDto(
        List<UUID> indexInfoIds,

        @NotNull
        LocalDate baseDateFrom,

        @NotNull
        LocalDate baseDateTo
) {
}
