package org.codeiteam3.findex.indexdata.mapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.enums.PeriodType;
import org.codeiteam3.findex.indexdata.dto.ChartDataPoint;
import org.codeiteam3.findex.indexdata.dto.IndexChartDto;
import org.codeiteam3.findex.indexinfo.entity.IndexInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface IndexChartMapper {

    @Mapping(target = "indexInfoId", source = "indexInfo.id")
    @Mapping(target = "indexClassification", source = "indexInfo.indexClassification")
    @Mapping(target = "indexName", source = "indexInfo.indexName")
    IndexChartDto toDto(IndexInfo indexInfo,
                               PeriodType periodType,
                               List<ChartDataPoint> price,
                               List<ChartDataPoint> avg5,
                               List<ChartDataPoint> avg20);
}
