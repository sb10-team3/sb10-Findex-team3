package org.codeiteam3.findex.indexdata.repository;

import org.codeiteam3.findex.indexdata.entity.IndexData;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IndexDataRepositoryQueryDsl {
    List<IndexData> findChartDataByPeriod(
            UUID indexInfoId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<IndexData> findLatestDataOfAllIndexesOnOrBefore(LocalDate targetDate);

    List<IndexData> findFavoriteDataOnOrBefore(LocalDate targetDate);

    Optional<LocalDate> findLatestBaseDateByIndexInfoId(UUID indexInfoId);

}
