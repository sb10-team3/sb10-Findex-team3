package org.codeiteam3.findex.indexinfo.dto.data;

import java.util.UUID;

public record IndexInfoSummaryDto(
    UUID id,
    String indexClassification,
    String indexName

) {
}
