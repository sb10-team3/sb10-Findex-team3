package org.codeiteam3.findex.autosyncconfig.service;

import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.autosyncconfig.repository.AutoSyncConfigRepository;
import org.codeiteam3.findex.syncjob.dto.IndexDataSyncRequestDto;
import org.codeiteam3.findex.syncjob.repository.SyncJobRepository;
import org.codeiteam3.findex.syncjob.service.SyncJobService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AutoSyncScheduler {
    private final SyncJobService syncJobService;
    private final AutoSyncConfigRepository autoSyncConfigRepository;
    private final SyncJobRepository syncJobRepository;

    @Scheduled(cron = "${findex.api.sync_cron}")
    public void schedule() {
        List<UUID> synclist = autoSyncConfigRepository.findEnabledWithIndexInfo().stream()
                .map(a -> a.getIndexInfo().getId())
                .toList();

        for(UUID id : synclist){
            LocalDate baseDateFrom = syncJobRepository.findLastJobTime(id,"system")
                    .orElse(LocalDate.now().minusDays(1));
            IndexDataSyncRequestDto dto = new IndexDataSyncRequestDto(
                    List.of(id),
                    baseDateFrom,
                    LocalDate.now()
            );
            syncJobService.indexDataSyncJob("system", dto);
        }
    }
}
