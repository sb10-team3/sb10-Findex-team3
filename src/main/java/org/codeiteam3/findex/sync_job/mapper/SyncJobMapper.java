package org.codeiteam3.findex.sync_job.mapper;

import org.codeiteam3.findex.sync_job.SyncJob;
import org.codeiteam3.findex.sync_job.dto.SyncJobDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SyncJobMapper {
    SyncJobDto toDto(SyncJob syncJob);
}
