package org.codeiteam3.findex.indexdata.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.codeiteam3.findex.enums.PeriodType;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class IndexChartDto {
    private UUID indexInfoId;
    private String indexClassification;
    private String indexName;
    private PeriodType periodType;
    private List<ChartDataPoint> dataPoints;
    private List<ChartDataPoint> ma5DataPoints;
    private List<ChartDataPoint> ma20DataPoints;
}
