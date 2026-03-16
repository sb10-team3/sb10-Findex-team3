package org.codeiteam3.findex.indexdata.service;

import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.enums.PeriodType;
import org.codeiteam3.findex.indexdata.dto.ChartDataPoint;
import org.codeiteam3.findex.indexdata.dto.IndexChartDto;
import org.codeiteam3.findex.indexdata.dto.IndexPerformanceDto;
import org.codeiteam3.findex.indexdata.dto.RankedIndexPerformanceDto;
import org.codeiteam3.findex.indexdata.entity.IndexData;
import org.codeiteam3.findex.indexdata.mapper.ChartDataMapper;
import org.codeiteam3.findex.indexdata.mapper.IndexChartMapper;
import org.codeiteam3.findex.indexdata.mapper.IndexPerformanceMapper;
import org.codeiteam3.findex.indexdata.mapper.RankedIndexPerformanceMapper;
import org.codeiteam3.findex.indexdata.repository.IndexDataRepository;
import org.codeiteam3.findex.indexinfo.entity.IndexInfo;
import org.codeiteam3.findex.indexinfo.repository.IndexInfoRepository;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndexDashBoardService {
    private final IndexDataRepository indexDataRepository;
    private final IndexChartMapper indexChartMapper;
    private final ChartDataMapper chartDataMapper;
    private final IndexInfoRepository indexInfoRepository;
    private final IndexPerformanceMapper indexPerformanceMapper;
    private final RankedIndexPerformanceMapper rankedIndexPerformanceMapper;

    @Transactional(readOnly = true)
    public IndexChartDto findIndexChart(UUID indexInfoId, PeriodType periodType){
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = calculatePastDate(startDate, periodType);

        List<IndexData> dataList = indexDataRepository.findChartDataByPeriod(indexInfoId, endDate,startDate);
        IndexInfo indexInfo = indexInfoRepository.findById(indexInfoId).orElseThrow(() -> new NoSuchElementException(indexInfoId + " 지수정보가 없습니다."));

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

    @Transactional(readOnly = true)
    public List<RankedIndexPerformanceDto> findIndexPerformanceRank(UUID indexInfoId, PeriodType periodType, Integer limit){
        LocalDate today = LocalDate.now();
        LocalDate past = calculatePastDate(today, periodType);

        List<IndexData> todayIndexDatas;
        List<IndexData> pastIndexDatas;

        // 특정 지수만 조회
        if(indexInfoId != null){
            todayIndexDatas = indexDataRepository.findTop1ByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(indexInfoId,today);
            pastIndexDatas = indexDataRepository.findTop1ByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(indexInfoId, past);
        // 전체 지수 조회
        } else{
            todayIndexDatas =  indexDataRepository.findLatestDataOfAllIndexesOnOrBefore(today);
            pastIndexDatas =  indexDataRepository.findLatestDataOfAllIndexesOnOrBefore(past);
        }


        // 지수 성과 Dto리스트 생성
        List<IndexPerformanceDto> performanceDtos =
                calculateBasePerformance(todayIndexDatas,pastIndexDatas,periodType).stream()
                // 등락률 기준으로 내림차순 정렬
                .sorted((a, b) -> {
                    // NullPointerException 방어 (안전한 정렬을 위해 null을 0으로 취급)
                    BigDecimal rateA = a.getFluctuationRate() != null ? a.getFluctuationRate() : BigDecimal.ZERO;
                    BigDecimal rateB = b.getFluctuationRate() != null ? b.getFluctuationRate() : BigDecimal.ZERO;
                    return rateB.compareTo(rateA);
                })
                // 특정 지수면 최신것만, 아니라면 limit까지 보여줌
                .limit((indexInfoId != null) ? limit : 1)
                .toList();

        // RankedPerformanceDto로 반환
        List<RankedIndexPerformanceDto> rankedList = new ArrayList<>();
        for(int i = 0; i < performanceDtos.size(); i++){
            rankedList.add(rankedIndexPerformanceMapper.toDto(performanceDtos.get(i), i + 1));
        }

        return rankedList;
    }

    @Transactional(readOnly = true)
    public List<IndexPerformanceDto> findFavoriteIndexPerformance(PeriodType periodType){
        LocalDate today = LocalDate.now();
        LocalDate past = calculatePastDate(today, periodType);

        List<IndexData> todayIndexDatas;
        List<IndexData> pastIndexDatas;

        todayIndexDatas = indexDataRepository.findFavoriteDataOnOrBefore(today);
        pastIndexDatas = indexDataRepository.findFavoriteDataOnOrBefore(past);

        return calculateBasePerformance(todayIndexDatas, pastIndexDatas, periodType);

    }

    // PeriodType에 따른 대비 기간
    private LocalDate calculatePastDate(LocalDate today, PeriodType periodType){
        return switch(periodType){
            case DAILY -> today.minusDays(1);
            case WEEKLY -> today.minusWeeks(1);
            case MONTHLY -> today.minusMonths(1);
            case QUARTERLY -> today.minusMonths(3);
            case YEARLY -> today.minusYears(1);
            default -> throw new IllegalArgumentException("지원하지 않는 기간 유형입니다: " + periodType);
        };
    }

    // 지수 성과 Dto리스트 생성
    private List<IndexPerformanceDto> calculateBasePerformance(List<IndexData> todayIndexData,
                                                               List<IndexData> pastIndexData,
                                                               PeriodType periodType){
        // 빠르게 데이터를 꺼내기 위해 map활용
        Map<UUID, IndexData> pastDataMap = pastIndexData.stream()
                .collect(Collectors.toMap(data -> data.getIndexInfo().getId(), data -> data));

        // indexPerformanceDto로 변환
        return todayIndexData.stream()
                .map(todayData -> {
                    IndexData pastData = pastDataMap.get(todayData.getIndexInfo().getId());

                    BigDecimal currentPrice = todayData.getClosingPrice();
                    BigDecimal beforePrice = BigDecimal.ZERO;
                    BigDecimal fluctuationRate = BigDecimal.ZERO;
                    BigDecimal versus = BigDecimal.ZERO;

                    // 일간 기준이면 todayData의 값을 사용한다(indexData에 전일 데이터가 있음)
                    if (periodType == PeriodType.DAILY) {
                        fluctuationRate = (todayData.getFluctuationRate() != null)
                                ? todayData.getFluctuationRate() : BigDecimal.ZERO;
                        versus = (todayData.getVersus() != null)
                                ? todayData.getVersus() : BigDecimal.ZERO;
                        beforePrice = currentPrice.subtract(versus);
                    } else {
                        if (pastData != null && pastData.getClosingPrice() != null) {
                            beforePrice = pastData.getClosingPrice();
                            // 전 가격이 0아 아니라면
                            if (beforePrice.compareTo(BigDecimal.ZERO) != 0) {
                                // 대비
                                versus = currentPrice.subtract(beforePrice);
                                // 등략률
                                // 등락률 계산 = 현재가 - 과거가 / 과거가 * 100
                                fluctuationRate = versus.divide(beforePrice, 2, RoundingMode.HALF_UP)
                                        .multiply(new BigDecimal("100"));
                            }
                        }
                    }
                    return indexPerformanceMapper.toDto(todayData, versus, fluctuationRate, currentPrice, beforePrice);
                }).toList();
    }
}
