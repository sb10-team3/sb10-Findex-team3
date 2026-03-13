package org.codeiteam3.findex.sync_job.mapper;

import org.codeiteam3.findex.sync_job.SyncJob;
import org.codeiteam3.findex.sync_job.dto.SyncJobDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SyncJobMapper {
    @Mapping(target = "id", source = "indexInfo.id")
    SyncJobDto toDto(SyncJob syncJob);
}
