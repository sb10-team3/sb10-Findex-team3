package org.codeiteam3.findex.autosyncconfig.dto;

import java.util.UUID;

public record AutoSyncConfigResponseDto(
        UUID id,
        UUID indexInfoId,
        String indexClassification,
        String indexName,
        boolean enabled
) {
}
