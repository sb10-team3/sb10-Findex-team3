package org.codeiteam3.findex.indexdata.dto;

import jakarta.validation.constraints.NotNull;
import org.codeiteam3.findex.SourceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record IndexDataCreateRequest(
        @NotNull
        UUID indexInfoId,

        @NotNull
        LocalDate baseDate,

        @NotNull
        BigDecimal marketPrice,

        @NotNull
        BigDecimal closingPrice,

        @NotNull
        BigDecimal highPrice,

        @NotNull
        BigDecimal lowPrice,

        @NotNull
        BigDecimal versus,

        @NotNull
        BigDecimal fluctuationRate,

        @NotNull
        Long tradingQuantity,

        @NotNull
        Long tradingPrice,

        @NotNull
        Long marketTotalAmount
) {
}
