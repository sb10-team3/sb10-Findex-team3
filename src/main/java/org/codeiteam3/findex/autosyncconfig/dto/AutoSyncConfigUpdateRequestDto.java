package org.codeiteam3.findex.autosyncconfig.dto;

import jakarta.validation.constraints.NotNull;

public record AutoSyncConfigUpdateRequestDto(
        @NotNull
        boolean enabled
) {
}
