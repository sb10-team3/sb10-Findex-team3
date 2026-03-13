package org.codeiteam3.findex.indexdata.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.codeiteam3.findex.enums.PeriodType;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(description = "지수 차트 데이터 DTO")
public class IndexChartDto {
    @NotNull
    @Schema(description = "지수 정보 ID", example = "11111111-1111-1111-1111-111111111111")
    private UUID indexInfoId;

    @NotNull
    @Schema(description = "지수 분류명" , example = "KOSPI시리즈")
    private String indexClassification;

    @NotNull
    @Schema(description = "지수명", example = "IT 서비스")
    private String indexName;

    @NotNull
    @Schema(description = "차트 기간 유형",
            allowableValues = {"MONTHLY", "QUARTERLY", "YEARLY"},
            example = "MONTHLY")
    private PeriodType periodType;

    @NotNull
    @Schema(description = "차트 데이터 목록")
    private List<ChartDataPoint> dataPoints;

    @Schema(description = "5일 이동평균선 데이터 목록")
    private List<ChartDataPoint> ma5DataPoints;

    @Schema(description = "20일 이동평균선 데이터 목록")
    private List<ChartDataPoint> ma20DataPoints;
}
