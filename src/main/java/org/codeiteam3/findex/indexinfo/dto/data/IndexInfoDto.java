package org.codeiteam3.findex.indexinfo.dto.data;

import org.codeiteam3.findex.SourceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record IndexInfoDto(
        UUID id,
        String indexClassification,
        String indexName,
        Integer employedItemsCount,
        LocalDate basePointInTime,
        BigDecimal baseIndex,
        SourceType sourceType,
        Boolean favorite
) {}

