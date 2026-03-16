package org.codeiteam3.findex.indexinfo.service;

import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.autosyncconfig.AutoSyncConfig;
import org.codeiteam3.findex.autosyncconfig.repository.AutoSyncConfigRepository;
import org.codeiteam3.findex.enums.SourceType;
import org.codeiteam3.findex.indexinfo.dto.request.IndexInfoCreateRequest;
import org.codeiteam3.findex.indexinfo.entity.IndexInfo;
import org.codeiteam3.findex.indexinfo.repository.IndexInfoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Set;
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
    public CursorPageResponseIndexInfoDto findAll(String indexClassification,String indexName,Boolean favorite,Integer idAfter,String cursor,String sortField,String sortDirection,Integer size){

        // cursor
        String normalizedCursor  = (cursor == null || cursor.isBlank()) ? null : cursor;

        //sortField
        Set<String> allowField = Set.of("indexClassification","indexName","employedItemsCount");
        if(!allowField.contains(sortField)){
            throw new IllegalArgumentException("적합하지 않은 정렬필드(sortField)입니다.");
        }
        String normalizedSortField = sortField;

        //정렬방향
        //Sort.Direction: Spring Data에서 사용하는 정렬 방향 enum
        //Sort.Direction.ASC
        //SORT.Direction.DESC
        //SortDirection이 asc이면 ASC 아니면 DESC
        Sort.Direction normalizedDirection = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        //Pageable(cursor, sortField, 정렬, 갯수 적용)
        //.and로 정렬조건 추가 SortField로 정렬이 안될경우를 대비해서 id로 정렬조건 추가
        Pageable pageable = PageRequest.of(0, size, Sort.by(normalizedDirection, normalizedSortField).and(Sort.by(normalizedDirection,"id")));

        return null;

    }

    @Transactional(readOnly = true)
    public IndexInfo findById(UUID id){
        return indexInfoRepository.findById(id).orElseThrow(() -> new NoSuchElementException(id + " 지수 정보가 없습니다."));

    }

}
