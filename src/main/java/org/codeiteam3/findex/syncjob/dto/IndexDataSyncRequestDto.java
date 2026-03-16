package org.codeiteam3.findex.syncjob.dto;

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
