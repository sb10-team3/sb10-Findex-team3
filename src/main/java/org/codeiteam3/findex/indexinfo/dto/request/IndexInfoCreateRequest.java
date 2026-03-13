package org.codeiteam3.findex.indexinfo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexInfoCreateRequest(

        @NotBlank(message = "지수 분류명은 필수입니다.")//@NotBlank에 null 검증 포함
        String indexClassification,

        @NotBlank(message = "지수명은 필수입니다.")
        String indexName,

        @NotNull
        Integer employedItemsCount,

        @NotNull(message = "기준 시점은 필수입니다.")
        LocalDate basePointInTime,

        @NotNull(message = "기준 지수는 필수입니다.")
        @DecimalMin(value = "0.0", inclusive = false, message = "기준 지수는 0보다 커야 합니다.")
        @Digits(integer = 16, fraction = 2, message = "기준 지수 형식이 올바르지 않습니다.")
        BigDecimal baseIndex,

        Boolean favorite
) {
}
