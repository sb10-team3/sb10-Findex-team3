package org.codeiteam3.findex.sync_job.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.indexdata.repository.IndexDataRepository;
import org.codeiteam3.findex.indexinfo.IndexInfo;
import org.codeiteam3.findex.indexinfo.repository.IndexInfoRepository;
import org.codeiteam3.findex.sync_job.JobType;
import org.codeiteam3.findex.sync_job.SyncJob;
import org.codeiteam3.findex.sync_job.dto.IndexApiResponseDto;
import org.codeiteam3.findex.sync_job.dto.IndexApiResponseItemDto;
import org.codeiteam3.findex.sync_job.dto.SyncJobDto;
import org.codeiteam3.findex.sync_job.exception.ExternalApiException;
import org.codeiteam3.findex.sync_job.mapper.SyncJobMapper;
import org.codeiteam3.findex.sync_job.repository.SyncJobRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.codeiteam3.findex.SourceType.OPEN_API;
import static org.codeiteam3.findex.sync_job.Result.FAILURE;
import static org.codeiteam3.findex.sync_job.Result.SUCCESS;

@Service
@RequiredArgsConstructor
public class SyncJobService {
    private final SyncJobRepository syncJobRepository;
    private final IndexDataRepository indexDataRepository;
    private final IndexInfoRepository indexInfoRepository;
    private final WebClient webClient;
    private final SyncJobMapper syncJobMapper;

    private final String API_KEY = "5c1a32de77483aa31eb13746d9abd7b75b08d47e2d2256a38cda7a8c18f39d91";

    public List<SyncJobDto> indexInfoSyncJob(String worker) {
        List<SyncJobDto> dtoList = new ArrayList<>();

        int pageNo = 1;
        int numOfRows = 100;

        while(true){
            try{
                int finalPageNo = pageNo;
                IndexApiResponseDto indexApiResponse = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("serviceKey", API_KEY)
                                .queryParam("resultType", "json")
                                .queryParam("pageNo", finalPageNo)
                                .queryParam("numOfRows", numOfRows)
                                .queryParam("basDt", LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
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

                for(IndexApiResponseItemDto item : items){
                    SyncJob syncJob = indexInfoSync(item, worker);
                    syncJobRepository.save(syncJob);
                    dtoList.add(syncJobMapper.toDto(syncJob));
                }
            }catch (Exception e){
                System.out.println("API 호출 실패: " + e.getMessage());
                e.printStackTrace();
                break; // 무한 루프 방지
            }

            pageNo++;
        }

        return dtoList;
    }

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

            return new SyncJob(
                    indexInfo,
                    JobType.INDEX_INFO,
                    null,
                    worker,
                    LocalDate.now(),
                    FAILURE
            );
        }

        return new SyncJob(
                indexInfo,
                JobType.INDEX_INFO,
                null,
                worker,
                LocalDate.now(),
                SUCCESS
        );
    }
}
