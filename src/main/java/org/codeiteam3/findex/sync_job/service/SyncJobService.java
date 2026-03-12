package org.codeiteam3.findex.sync_job.service;

import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.sync_job.repository.SyncJobRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SyncJobService {
    private final SyncJobRepository syncJobRepository;

}
