package org.codeiteam3.findex.indexdata.service;

import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.enums.PeriodType;
import org.codeiteam3.findex.indexdata.dto.ChartDataPoint;
import org.codeiteam3.findex.indexdata.dto.IndexChartDto;
import org.codeiteam3.findex.indexdata.entity.IndexData;
import org.codeiteam3.findex.indexdata.mapper.ChartDataMapper;
import org.codeiteam3.findex.indexdata.mapper.IndexChartMapper;
import org.codeiteam3.findex.indexdata.repository.IndexDataRepository;
import org.codeiteam3.findex.indexinfo.entity.IndexInfo;
import org.codeiteam3.findex.indexinfo.repository.IndexInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class IndexDashBoardService {
    private final IndexDataRepository indexDataRepository;
    private final IndexChartMapper indexChartMapper;
    private final ChartDataMapper chartDataMapper;
    private final IndexInfoRepository indexInfoRepository;

    @Transactional(readOnly = true)
    public IndexChartDto find(UUID indexInfoId, PeriodType periodType){
        List<IndexData> dataList = indexDataRepository.findByIndexInfoIdOrderByBaseDate(indexInfoId);
        IndexInfo indexInfo = indexInfoRepository.findById(indexInfoId)
                .orElseThrow(() -> new NoSuchElementException());


        // 5일 이동평균선 데이터
        Queue<BigDecimal> window5 = new LinkedList<>();
        BigDecimal sum5 = BigDecimal.ZERO;

        // 5일 이동평균선 데이터
        Queue<BigDecimal> window20 = new LinkedList<>();
        BigDecimal sum20 = BigDecimal.ZERO;

        // 값을 담을 리스트
        List<ChartDataPoint> priceList = new ArrayList<>();
        List<ChartDataPoint> avg5List = new ArrayList<>();
        List<ChartDataPoint> avg20List = new ArrayList<>();

        for(IndexData data : dataList){
            // 오늘의 종가
            BigDecimal price = (data.getClosingPrice() != null) ? data.getClosingPrice() : BigDecimal.ZERO;
            // 데이터마다 종가 담기
            priceList.add(chartDataMapper.toDto(data.getBaseDate(), price));
            // 데이터를 하나씩 읽으면서 기준 일치에 맞게 큐에 넣고 총합 구하기
            window5.add(price);
            sum5 = sum5.add(price);
            if(window5.size() > 5){
                sum5 = sum5.subtract(window5.poll());
            }

            window20.add(price);
            sum20 = sum20.add(price);
            if(window20.size() > 20){
                sum20 = sum20.subtract(window20.poll());
            }

            // 평균 종가
            BigDecimal priceAverage5 = null;
            BigDecimal priceAverage20 = null;

            // 이동평균선에 따른 평균 담기
            if(window5.size() == 5){
                priceAverage5 = sum5.divide(new BigDecimal("5"), 2, RoundingMode.HALF_UP);
                avg5List.add(chartDataMapper.toDto(data.getBaseDate(), priceAverage5));
            }
            if(window20.size() == 20){
                priceAverage20 = sum20.divide(new BigDecimal("20"),2,RoundingMode.HALF_UP);
                avg20List.add(chartDataMapper.toDto(data.getBaseDate(), priceAverage20));
            }
        }

        return indexChartMapper.toDto(indexInfo, periodType, priceList, avg5List, avg20List);
    }
}
