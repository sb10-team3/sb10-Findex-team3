package org.codeiteam3.findex.indexdata.mapper;

import org.codeiteam3.findex.indexdata.dto.IndexPerformanceDto;
import org.codeiteam3.findex.indexdata.dto.RankedIndexPerformanceDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RankedIndexPerformanceMapper {
    RankedIndexPerformanceDto toDto(IndexPerformanceDto performance, Integer rank);
}
