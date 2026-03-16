package org.codeiteam3.findex.indexdata.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Schema(description = "지수 데이터 수정 요청")
public record IndexDataUpdateRequest(

        @Positive
        @Schema(description = "시가", example = "2800.25")
        BigDecimal marketPrice,

        @Positive
        @Schema(description = "종가", example = "2850.75")
        BigDecimal closingPrice,

        @Positive
        @Schema(description = "고가", example = "2870.5")
        BigDecimal highPrice,

        @Positive
        @Schema(description = "저가", example = "2795.3")
        BigDecimal lowPrice,

        @Schema(description = "전일 대비 등락", example = "50.5")
        BigDecimal versus,

        @Schema(description = "전일 대비 등락률", example = "1.8")
        BigDecimal fluctuationRate,

        @PositiveOrZero
        @Schema(description = "거래량", example = "1250000")
        Long tradingQuantity,

        @PositiveOrZero
        @Schema(description = "거래대금", example = "3500000000")
        Long tradingPrice,

        @PositiveOrZero
        @Schema(description = "상장 시가 총액", example = "450000000000")
        Long marketTotalAmount
) {
}
