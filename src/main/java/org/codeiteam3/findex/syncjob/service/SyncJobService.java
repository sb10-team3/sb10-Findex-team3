package org.codeiteam3.findex.syncjob.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.autosyncconfig.AutoSyncConfig;
import org.codeiteam3.findex.autosyncconfig.repository.AutoSyncConfigRepository;
import org.codeiteam3.findex.autosyncconfig.service.AutoSyncConfigService;
import org.codeiteam3.findex.enums.Result;
import org.codeiteam3.findex.indexdata.entity.IndexData;
import org.codeiteam3.findex.indexdata.repository.IndexDataRepository;
import org.codeiteam3.findex.indexinfo.entity.IndexInfo;
import org.codeiteam3.findex.indexinfo.repository.IndexInfoRepository;
import org.codeiteam3.findex.enums.JobType;
import org.codeiteam3.findex.syncjob.dto.IndexDataSyncRequestDto;
import org.codeiteam3.findex.syncjob.entity.SyncJob;
import org.codeiteam3.findex.syncjob.dto.IndexApiResponseDto;
import org.codeiteam3.findex.syncjob.dto.IndexApiResponseItemDto;
import org.codeiteam3.findex.syncjob.dto.SyncJobDto;
import org.codeiteam3.findex.syncjob.exception.ExternalApiException;
import org.codeiteam3.findex.syncjob.mapper.SyncJobMapper;
import org.codeiteam3.findex.syncjob.repository.SyncJobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.codeiteam3.findex.enums.SourceType.OPEN_API;
import static org.codeiteam3.findex.enums.Result.FAILURE;
import static org.codeiteam3.findex.enums.Result.SUCCESS;

@Service
@RequiredArgsConstructor
public class SyncJobService {
    private final SyncJobRepository syncJobRepository;
    private final IndexDataRepository indexDataRepository;
    private final IndexInfoRepository indexInfoRepository;
    private final WebClient webClient;
    private final SyncJobMapper syncJobMapper;
    private final AutoSyncConfigRepository autoSyncConfigRepository;

//    private final String API_KEY = "5c1a32de77483aa31eb13746d9abd7b75b08d47e2d2256a38cda7a8c18f39d91";

    @Value("${findex.api.key}")
    private String API_KEY;
    //지수 정보
    public List<SyncJobDto> indexInfoSyncJob(String worker) {
        List<SyncJobDto> dtoList = new ArrayList<>();

        int pageNo = 1;
        int numOfRows = 100;
        int minusday = 1;
        boolean dateConfirmed = false;

        while(true){
            try{
                int finalPageNo = pageNo;
                int finalMinusday = minusday;
                IndexApiResponseDto indexApiResponse = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("serviceKey", API_KEY)
                                .queryParam("resultType", "json")
                                .queryParam("pageNo", finalPageNo)
                                .queryParam("numOfRows", numOfRows)
                                .queryParam("basDt", LocalDate.now().minusDays(finalMinusday).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                                .build()
                        )
                        .retrieve()
                        .bodyToMono(IndexApiResponseDto.class)
                        .block();

                if(indexApiResponse == null){
                    throw new ExternalApiException("외부 api 응답이 null입니다.");
                }

                if(indexApiResponse.response() == null){
                    throw new ExternalApiException("외부 api 응답이 null입니다.");
                }

                if(indexApiResponse.response().body() == null){
                    break;
                }

                if(indexApiResponse.response().body().items() == null){
                    break;
                }

                List<IndexApiResponseItemDto> items = indexApiResponse.response().body().items().item();

                if(items == null || items.isEmpty()){
                    if(!dateConfirmed){
                        minusday++;
                        continue;
                    }
                    else{
                        break;
                    }
                }
                dateConfirmed=true;

                for(IndexApiResponseItemDto item : items){
                    SyncJob syncJob = indexInfoSync(item, worker);
                    syncJobRepository.save(syncJob);
                    dtoList.add(syncJobMapper.toDto(syncJob));
                }
            }catch (Exception e) {
                System.out.println("API 호출 실패: " + e.getMessage());
                e.printStackTrace();
                break; // 무한 루프 방지
            }
            pageNo++;
        }

        return dtoList;
    }

    //내부에서 쓰는 메서드
    @Transactional
    public SyncJob indexInfoSync(IndexApiResponseItemDto item, String worker){
        IndexInfo indexInfo = indexInfoRepository.findByIndexClassificationAndIndexNameAndSourceType(
                item.idxCsf(),
                item.idxNm(),
                OPEN_API
        );

        try{
            //이미 db에 존재할 경우
            if(indexInfo != null){
                indexInfo.update(
                        Integer.parseInt(item.epyItmsCnt()),
                        LocalDate.parse(item.basDt(), DateTimeFormatter.ofPattern("yyyyMMdd")),
                        new BigDecimal(item.basIdx()),
                        null
                );

                indexInfoRepository.save(indexInfo);

            }
            //없으면 새로 생성
            else{
                indexInfo = new IndexInfo(
                        item.idxCsf(),
                        item.idxNm(),
                        Integer.parseInt(item.epyItmsCnt()),
                        LocalDate.parse(item.basDt(), DateTimeFormatter.ofPattern("yyyyMMdd")),
                        new BigDecimal(item.basIdx()),
                        OPEN_API,
                        false
                );
                indexInfoRepository.save(indexInfo);
                if(autoSyncConfigRepository.findByIndexInfoId(indexInfo.getId()) == null){
                    autoSyncConfigRepository.save(new AutoSyncConfig(indexInfo, false));
                }
            }
        }catch (DataAccessException e){
            indexInfo = new IndexInfo(
                    item.idxCsf(),
                    item.idxNm(),
                    Integer.parseInt(item.epyItmsCnt()),
                    LocalDate.parse(item.basDt(), DateTimeFormatter.ofPattern("yyyyMMdd")),
                    new BigDecimal(item.basIdx()),
                    OPEN_API,
                    false
            );

            return toSyncJob(
                    indexInfo,
                    JobType.INDEX_INFO,
                    null,
                    worker,
                    LocalDate.now(),
                    FAILURE
            );
        }

        return toSyncJob(
                indexInfo,
                JobType.INDEX_INFO,
                null,
                worker,
                LocalDate.now(),
                SUCCESS
        );
    }

    //여기부터 지수 데이터
    public List<SyncJobDto> indexDataSyncJob(String worker, IndexDataSyncRequestDto requestDto){
        int pageNo = 1;
        int numOfRows = 100;

        List<SyncJobDto> dtoList = new ArrayList<>();

        List<IndexInfo> indexInfoList = indexInfoRepository.findAllById(requestDto.indexInfoIds());

        for(IndexInfo indexInfo : indexInfoList){
            while(true){
                int finalPageNo = pageNo;
                IndexApiResponseDto indexApiResponse = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("serviceKey", API_KEY)
                                .queryParam("resultType", "json")
                                .queryParam("idxNm", indexInfo.getIndexName())
                                .queryParam("beginBasDt", requestDto.baseDateFrom().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                                .queryParam("endBasDt", requestDto.baseDateTo().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                                .queryParam("pageNo", finalPageNo)
                                .queryParam("numOfRows", numOfRows)
                                .build()
                        )
                        .retrieve()
                        .bodyToMono(IndexApiResponseDto.class)
                        .block();

                if(indexApiResponse == null){
                    throw new ExternalApiException("외부 api 응답이 null입니다.");
                }

                if(indexApiResponse.response() == null){
                    throw new ExternalApiException("외부 api 응답이 null입니다.");
                }

                if(indexApiResponse.response().body() == null){
                    break;
                }

                if(indexApiResponse.response().body().items() == null){
                    break;
                }

                List<IndexApiResponseItemDto> items = indexApiResponse.response().body().items().item();

                if(items == null || items.isEmpty()){
                    break;
                }

                items = items.stream()
                        .filter(item -> item.idxCsf().equals(indexInfo.getIndexClassification()))
                        .toList();

                for(IndexApiResponseItemDto item : items){
                    SyncJob syncJob = indexDataSync(item, indexInfo, worker);
                    syncJobRepository.save(syncJob);
                    dtoList.add(syncJobMapper.toDto(syncJob));
                }

                int totalCount = indexApiResponse.response().body().totalCount();

                if(pageNo * numOfRows >= totalCount){
                    break;
                }

                pageNo++;

            }
            pageNo = 1;
        }

        return dtoList;
    }

    //데이터 연동과정에서 쓰는 메서드
    private SyncJob indexDataSync(IndexApiResponseItemDto item, IndexInfo indexInfo, String worker){
        try{
            //이미 있으면 갱신 안함
            if(indexDataRepository.existsByIndexInfoIndexNameAndIndexInfoIndexClassificationAndBaseDate(
                    indexInfo.getIndexName(),
                    indexInfo.getIndexClassification(),
                    LocalDate.parse(item.basDt(), DateTimeFormatter.ofPattern("yyyyMMdd")))
            ){
                return toSyncJob(
                        indexInfo,
                        JobType.INDEX_DATA,
                        LocalDate.parse(item.basDt(), DateTimeFormatter.ofPattern("yyyyMMdd")),
                        worker,
                        LocalDate.now(),
                        FAILURE
                );
            }
            else{
                indexDataRepository.save(new IndexData(
                        indexInfo,
                        LocalDate.parse(item.basDt(), DateTimeFormatter.ofPattern("yyyyMMdd")),
                        OPEN_API,
                        new BigDecimal(item.mkp()),
                        new BigDecimal(item.clpr()),
                        new BigDecimal(item.hipr()),
                        new BigDecimal(item.lopr()),
                        new BigDecimal(item.vs()),
                        new BigDecimal(item.fltRt()),
                        Long.parseLong(item.trqu()),
                        Long.parseLong(item.trPrc()),
                        Long.parseLong(item.lstgMrktTotAmt())
                ));

                return toSyncJob(
                        indexInfo,
                        JobType.INDEX_DATA,
                        LocalDate.parse(item.basDt(), DateTimeFormatter.ofPattern("yyyyMMdd")),
                        worker,
                        LocalDate.now(),
                        SUCCESS
                );
            }
        }catch (DataAccessException e){
            return toSyncJob(
                    indexInfo,
                    JobType.INDEX_DATA,
                    LocalDate.parse(item.basDt(), DateTimeFormatter.ofPattern("yyyyMMdd")),
                    worker,
                    LocalDate.now(),
                    FAILURE
            );
        }
    }


    private SyncJob toSyncJob(
            IndexInfo indexInfo,
            JobType jobType,
            LocalDate targetDate,
            String worker,
            LocalDate jobTime,
            Result result
    ){
        return new SyncJob(
                indexInfo,
                jobType,
                targetDate,
                worker,
                jobTime,
                result
        );
    }
}
