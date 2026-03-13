package org.codeiteam3.findex.indexinfo.mapper;


import org.codeiteam3.findex.indexinfo.dto.data.IndexInfoDto;
import org.codeiteam3.findex.indexinfo.entity.IndexInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IndexInfoMapper {

    IndexInfoDto toDto(IndexInfo indexInfo);

}
