package org.codeiteam3.findex.indexdata.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(description = "순위가 포함된 지수 성과 정보 DTO")
public class IndexPerformanceDto {
    @NotNull
    @Schema(description = "지수 정보 ID")
    private UUID indexInfoId;

    @NotNull
    @Schema(description = "지수 분류명", example = "KOSPI시리즈")
    private String indexClassification;

    @NotNull
    @Schema(description = "지수명", example = "IT 서비스")
    private String indexName;

    @NotNull
    @Schema(description = "단위 기간 대비 등락", example = "50.5")
    private BigDecimal versus;

    @NotNull
    @Schema(description = "단위 기간 대비 등락률", example = "1.8")
    private BigDecimal fluctuationRate;

    @NotNull
    @Schema(description = "현재가", example = "2850.75")
    private BigDecimal currentPrice;

    @NotNull
    @Schema(description = "단위 기간 전 값", example = "2850.75")
    private BigDecimal beforePrice;
}
