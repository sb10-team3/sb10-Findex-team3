package org.codeiteam3.findex.indexinfo.service;

import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.autosyncconfig.AutoSyncConfig;
import org.codeiteam3.findex.autosyncconfig.repository.AutoSyncConfigRepository;
import org.codeiteam3.findex.enums.SourceType;
import org.codeiteam3.findex.indexinfo.dto.request.IndexInfoCreateRequest;
import org.codeiteam3.findex.indexinfo.entity.IndexInfo;
import org.codeiteam3.findex.indexinfo.repository.IndexInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class IndexInfoService {

    private final IndexInfoRepository indexInfoRepository;
    private final AutoSyncConfigRepository autoSyncConfigRepository;

    public IndexInfo create(IndexInfoCreateRequest request){

        if (indexInfoRepository.existsByIndexClassificationAndIndexName(
                request.indexClassification(),
                request.indexName()
        )) {
            throw new IllegalArgumentException("이미 존재하는 지수입니다.");
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

        //자동 연동 설정 초기화
        AutoSyncConfig config = new AutoSyncConfig(saved, false);
        autoSyncConfigRepository.save(config);

        return saved;
    }

    @Transactional(readOnly = true)
    public IndexInfo findById(UUID id){
        return indexInfoRepository.findById(id).orElseThrow(() -> new NoSuchElementException(id + " 지수 정보가 없습니다."));

    }

}
