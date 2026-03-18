package org.codeiteam3.findex.autosyncconfig.mapper;

import org.codeiteam3.findex.autosyncconfig.entity.AutoSyncConfig;
import org.codeiteam3.findex.autosyncconfig.dto.AutoSyncConfigResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AutoSyncConfigMapper {
    @Mapping(source = "indexInfo.id", target = "indexInfoId")
    @Mapping(source = "indexInfo.indexClassification", target = "indexClassification")
    @Mapping(source = "indexInfo.indexName", target = "indexName")
    AutoSyncConfigResponseDto toDto(AutoSyncConfig autoSyncConfig);
}
