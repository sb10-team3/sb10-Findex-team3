package org.codeiteam3.findex.indexdata.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "순위가 포함된 지수 성과 정보 DTO")
public class RankedIndexPerformanceDto {
    @NotNull
    @Schema(description = "지수 성과 정보")
    private IndexPerformanceDto performance;

    @NotNull
    @Schema(description = "순위", example = "1")
    private Integer rank;

}
