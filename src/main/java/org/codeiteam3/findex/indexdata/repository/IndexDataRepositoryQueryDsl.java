package org.codeiteam3.findex.indexdata.repository;

import org.codeiteam3.findex.indexdata.entity.IndexData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IndexDataRepositoryQueryDsl {


    Long countElements(UUID indexInfoId, LocalDate startDate, LocalDate endDate);

    Slice<IndexData> findAllByBaseDate(
            UUID indexInfoId,
            LocalDate startDate,
            LocalDate endDate,
            UUID idAfter,
            LocalDate localDateCursor,
            Sort.Direction normalizedDirection,
            Pageable pageable
    );

    Slice<IndexData> findAllByBigDecimal(
            UUID indexInfoId,
            LocalDate startDate,
            LocalDate endDate,
            UUID idAfter,
            BigDecimal bigDecimalCursor,
            Sort.Direction normalizedDirection,
            String normalizedSortField,
            Pageable pageable
    );

    Slice<IndexData> findAllByLong(
            UUID indexInfoId,
            LocalDate startDate,
            LocalDate endDate,
            UUID idAfter,
            Long longCursor,
            Sort.Direction normalizedDirection,
            String normalizedSortField,
            Pageable pageable
    );

    List<IndexData> findChartDataByPeriod(
            UUID indexInfoId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<IndexData> findLatestDataOfAllIndexesOnOrBefore(LocalDate targetDate);

    List<IndexData> findFavoriteDataOnOrBefore(LocalDate targetDate);

    Optional<LocalDate> findLatestBaseDateByIndexInfoId(UUID indexInfoId);

}


