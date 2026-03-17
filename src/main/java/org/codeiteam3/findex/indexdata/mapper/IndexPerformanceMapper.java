package org.codeiteam3.findex.indexdata.mapper;

import org.codeiteam3.findex.indexdata.dto.IndexPerformanceDto;
import org.codeiteam3.findex.indexdata.entity.IndexData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface IndexPerformanceMapper {
    @Mapping(target = "indexInfoId", source = "indexData.indexInfo.id")
    @Mapping(target = "indexClassification", source = "indexData.indexInfo.indexClassification")
    @Mapping(target = "indexName", source = "indexData.indexInfo.indexName")
    @Mapping(source = "versus", target = "versus")
    @Mapping(source = "fluctuationRate", target = "fluctuationRate")
    IndexPerformanceDto toDto(IndexData indexData,
                              BigDecimal versus,
                              BigDecimal fluctuationRate,
                              BigDecimal currentPrice,
                              BigDecimal beforePrice);
}
