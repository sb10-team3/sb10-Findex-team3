package org.codeiteam3.findex.indexdata.mapper;

import org.codeiteam3.findex.indexdata.dto.IndexDataDto;
import org.codeiteam3.findex.indexdata.entity.IndexData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IndexDataMapper {

    @Mapping(source = "indexInfo.id", target = "indexInfoId")
    IndexDataDto toDto(IndexData indexData);
}
