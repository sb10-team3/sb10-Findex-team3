package org.codeiteam3.findex.indexinfo.service;

import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.SourceType;
import org.codeiteam3.findex.autosyncconfig.entity.AutoSyncConfig;
import org.codeiteam3.findex.autosyncconfig.repository.AutoSyncConfigRepository;
import org.codeiteam3.findex.indexinfo.dto.request.IndexInfoCreateRequest;
import org.codeiteam3.findex.indexinfo.entity.IndexInfo;
import org.codeiteam3.findex.indexinfo.repository.IndexInfoRespository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
@Service
@Transactional
public class IndexInfoService {

    private final IndexInfoRespository indexInfoRepository;
    private final AutoSyncConfigRepository autoSyncConfigRepository;

    public IndexInfo create(IndexInfoCreateRequest request){

        if (request.indexClassification() == null || request.indexClassification().isBlank()) {
            throw new IllegalArgumentException("지수 분류명은 필수입니다.");
        }

        if (request.indexName() == null || request.indexName().isBlank()) {
            throw new IllegalArgumentException("지수명은 필수입니다.");
        }
        if (indexInfoRepository.existsByIndexClassificationAndIndexName(
                request.indexClassification(),
                request.indexName()
        )) {
            throw new IllegalArgumentException("이미 존재하는 지수입니다.");
        }

        if (request.employedItemsCount() == null || request.employedItemsCount() <= 0) {
            throw new IllegalArgumentException("채용 종목 수는 1 이상이어야 합니다.");
        }

        if (request.baseIndex() == null || request.baseIndex().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("기준 지수는 0보다 커야 합니다.");
        }

        if (request.basePointInTime() == null) {
            throw new IllegalArgumentException("기준 시점은 필수입니다.");
        }

        if (request.basePointInTime().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("기준 시점은 미래일 수 없습니다.");
        }

        Boolean favorite = request.favorite() != null ? request.favorite() : false;

        IndexInfo indexInfo = new IndexInfo(
                request.indexClassification(),
                request.indexName(),
                request.employedItemsCount(),
                request.basePointInTime(),
                request.baseIndex(),
                SourceType.USER,
                favorite
        );
        IndexInfo saved = indexInfoRepository.save(indexInfo);
        AutoSyncConfig config = new AutoSyncConfig(saved, false);
        autoSyncConfigRepository.save(config);

        return saved;
    }

}
