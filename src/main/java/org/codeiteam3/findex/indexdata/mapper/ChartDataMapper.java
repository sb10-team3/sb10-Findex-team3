package org.codeiteam3.findex.indexdata.mapper;

import org.codeiteam3.findex.indexdata.dto.ChartDataPoint;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface ChartDataMapper {

    ChartDataPoint toDto(LocalDate baseData, BigDecimal price);
}
