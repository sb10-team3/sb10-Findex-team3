package org.codeiteam3.findex.indexinfo.service;

import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.autosyncconfig.entity.AutoSyncConfig;
import org.codeiteam3.findex.autosyncconfig.repository.AutoSyncConfigRepository;
import org.codeiteam3.findex.pagination.CursorPageResponse;
import org.codeiteam3.findex.pagination.CursorPageResponseMapper;
import org.codeiteam3.findex.enums.SourceType;
import org.codeiteam3.findex.indexinfo.dto.data.CursorPageResponseIndexInfoDto;
import org.codeiteam3.findex.indexinfo.dto.data.IndexInfoDto;
import org.codeiteam3.findex.indexinfo.dto.data.IndexInfoSummaryDto;
import org.codeiteam3.findex.indexinfo.dto.request.IndexInfoCreateRequest;
import org.codeiteam3.findex.indexinfo.dto.request.IndexInfoUpdateRequest;
import org.codeiteam3.findex.indexinfo.entity.IndexInfo;
import org.codeiteam3.findex.indexinfo.mapper.IndexInfoMapper;
import org.codeiteam3.findex.indexinfo.repository.IndexInfoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional
public class IndexInfoService {

    private final IndexInfoRepository indexInfoRepository;
    private final AutoSyncConfigRepository autoSyncConfigRepository;
    private final IndexInfoMapper indexInfoMapper;
    private final CursorPageResponseMapper cursorPageResponseMapper;

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
    public CursorPageResponseIndexInfoDto findAll(String indexClassification,String indexName,Boolean favorite,UUID idAfter,String cursor,String sortField,String sortDirection, int size){

        if (size < 1) {
            throw new IllegalArgumentException(" 페이지 크기(size)는 1 이상이어야 합니다.");
        }

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

        Pageable pageable = PageRequest.of(0, size);

        //조회된 전체 데이터 수
        Long totalElements = indexInfoRepository.countElements(indexClassification,indexName,favorite);

        //지수 정보 조회
        Slice<IndexInfoDto> indexInfoSlice = findIndexInfoSlice(indexClassification,indexName,favorite,idAfter,normalizedCursor,normalizedSortField,normalizedDirection,pageable)
                .map(indexInfoMapper::toDto);

        //조회정보 중 마지막 요소
        IndexInfoDto lastIndexInfo = !indexInfoSlice.getContent().isEmpty() ? indexInfoSlice.getContent().get(indexInfoSlice.getNumberOfElements() - 1) : null;

        String nextCursor = null; //sortField에 따라 달라짐
        UUID nextIdAfter = null;
        if(indexInfoSlice.hasNext()){
            nextCursor = findNextCursor(lastIndexInfo,normalizedSortField); //다음 페이지 커서
            nextIdAfter = lastIndexInfo.id(); //마지막 요소 ID
        }

        CursorPageResponse<IndexInfoDto> cursorPageResponse = cursorPageResponseMapper.fromSlice(
                indexInfoSlice,
                nextCursor,
                nextIdAfter,
                totalElements
        );



        return new CursorPageResponseIndexInfoDto(
                cursorPageResponse.content(),
                cursorPageResponse.nextCursor(),
                cursorPageResponse.nextIdAfter(),
                cursorPageResponse.size(),
                totalElements,
                cursorPageResponse.hasNext()
        );

    }
    private Slice<IndexInfo> findIndexInfoSlice(String indexClassification,
                                                String indexName,
                                                Boolean favorite,
                                                UUID idAfter,
                                                String normalizedCursor,
                                                String normalizedSortField,
                                                Sort.Direction normalizedDirection,
                                                Pageable pageable){
        return switch (normalizedSortField) {
            case "indexClassification","indexName" ->
                indexInfoRepository.findAllByString(
                        indexClassification,
                        indexName,
                        favorite,
                        idAfter,
                        normalizedCursor,
                        normalizedSortField,
                        normalizedDirection,
                        pageable
                );

            case "employedItemsCount" ->
                    indexInfoRepository.findAllByInteger(
                    indexClassification,
                    indexName,
                    favorite,
                    idAfter,
                    parseIntegerCursor(normalizedCursor),
                    normalizedDirection,
                    pageable
                    );
            default -> throw new IllegalArgumentException("제대로 되지 않은 sortField입니다.");
        };

    }
    // String -> Integer
    private Integer parseIntegerCursor(String cursor) {
        if (cursor == null) return null;
        return Integer.parseInt(cursor);
    }

    private String findNextCursor(IndexInfoDto lastIndexInfo, String normalizedSortedField){
        return switch (normalizedSortedField){
            case "indexClassification" -> lastIndexInfo.indexClassification();
            case "indexName" -> lastIndexInfo.indexName();
            case "employedItemsCount" -> lastIndexInfo.employedItemsCount().toString();
            default -> throw new IllegalStateException("제대로 되지 않은 sortField입니다.");
        };
    }

    @Transactional(readOnly = true)
    public IndexInfo findById(UUID id){
        return indexInfoRepository.findById(id).orElseThrow(() -> new NoSuchElementException(id + " 지수 정보가 없습니다."));

    }

    @Transactional(readOnly = true)
    public List<IndexInfoSummaryDto> findSummaries(){

        return indexInfoRepository.findAll().stream()
                .map(indexInfo -> new IndexInfoSummaryDto(
                        indexInfo.getId(),
                        indexInfo.getIndexClassification(),
                        indexInfo.getIndexName()
                ))
                .toList();


    }


    public IndexInfo update(UUID id, IndexInfoUpdateRequest indexInfoUpdateRequest){
        Integer employedItemsCount =  indexInfoUpdateRequest.employedItemsCount();
        LocalDate basePointInTime =  indexInfoUpdateRequest.basePointInTime();
        BigDecimal baseIndex =  indexInfoUpdateRequest.baseIndex();
        Boolean favorite = indexInfoUpdateRequest.favorite();

        IndexInfo indexInfo = indexInfoRepository.findById(id).orElseThrow(() -> new NoSuchElementException(id + " 에 해당하는 지수정보가 없습니다."));
        indexInfo.update(employedItemsCount,basePointInTime,baseIndex,favorite); //자동 save된다.

        return indexInfo;
    }

    public void delete(UUID id){
        IndexInfo indexInfo = indexInfoRepository.findById(id).orElseThrow(() -> new NoSuchElementException(id + " 에 해당하는 지수정보가 없습니다."));
        indexInfoRepository.delete(indexInfo);
    }

}
