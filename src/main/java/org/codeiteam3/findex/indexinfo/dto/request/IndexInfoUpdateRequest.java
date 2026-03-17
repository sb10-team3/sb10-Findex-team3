package org.codeiteam3.findex.indexinfo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexInfoUpdateRequest(


        Integer employedItemsCount,

        LocalDate basePointInTime,

        @DecimalMin(value = "0.0", inclusive = false, message = "기준 지수는 0보다 커야 합니다.")
        @Digits(integer = 16, fraction = 2, message = "기준 지수 형식이 올바르지 않습니다.")
        BigDecimal baseIndex,

        Boolean favorite
) {
}
