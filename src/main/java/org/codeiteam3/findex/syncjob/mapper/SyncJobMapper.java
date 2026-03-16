package org.codeiteam3.findex.syncjob.mapper;

import org.codeiteam3.findex.syncjob.entity.SyncJob;
import org.codeiteam3.findex.syncjob.dto.SyncJobDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SyncJobMapper {
    @Mapping(target = "indexInfoId", source = "indexInfo.id")
    SyncJobDto toDto(SyncJob syncJob);
}
