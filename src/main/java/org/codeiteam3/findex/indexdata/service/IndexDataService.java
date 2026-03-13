package org.codeiteam3.findex.indexdata.service;

import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.SourceType;
import org.codeiteam3.findex.indexdata.dto.IndexDataCreateRequest;
import org.codeiteam3.findex.indexdata.dto.IndexDataDto;
import org.codeiteam3.findex.indexdata.entity.IndexData;
import org.codeiteam3.findex.indexdata.mapper.IndexDataMapper;
import org.codeiteam3.findex.indexdata.repository.IndexDataRepository;
import org.codeiteam3.findex.indexinfo.IndexInfo;
import org.codeiteam3.findex.indexinfo.repository.IndexInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class IndexDataService {
    private final IndexDataRepository indexDataRepository;
    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataMapper indexDataMapper;

    public IndexDataDto create(IndexDataCreateRequest request) {
        UUID indexInfoId = request.indexInfoId();

        // 지수 데이터 존재 여부
        if (indexDataRepository.existsByIndexInfoIdAndBaseDate(indexInfoId, request.baseDate())) {
            throw new IllegalArgumentException("같은 지수와 날짜가 데이터에 이미 존재합니다.");
        }

        // 지수 데이터 생성
        IndexInfo indexInfo = indexInfoRepository.findById(request.indexInfoId())
                .orElseThrow(() ->new NoSuchElementException(indexInfoId + "를 가진 IndexInfo를 찾을 수 없습니다."));

        IndexData indexData = new IndexData(
                indexInfo,
                request.baseDate(),
                SourceType.USER,
                request.marketPrice(),
                request.closingPrice(),
                request.highPrice(),
                request.lowPrice(),
                request.versus(),
                request.fluctuationRate(),
                request.tradingQuantity(),
                request.tradingPrice(),
                request.marketTotalAmount()
        );
        indexDataRepository.save(indexData);

        return indexDataMapper.toDto(indexData);
    }
}
