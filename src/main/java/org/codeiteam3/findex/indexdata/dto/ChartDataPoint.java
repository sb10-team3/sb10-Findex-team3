package org.codeiteam3.findex.indexdata.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ChartDataPoint {
    private LocalDate date;
    private BigDecimal value;
}
