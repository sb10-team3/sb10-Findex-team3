package org.codeiteam3.findex.pagination;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Slice;

import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class CursorPageResponseMapper {

    public <T> CursorPageResponse<T> fromSlice(
            Slice<T> slice,
            String nextCursor,
            UUID nextIdAfter,
            Long totalElements)
    {
        if (!slice.hasNext()) {
            nextCursor = null;
            nextIdAfter = null;
        }
        return new CursorPageResponse<>(
                slice.getContent(),
                nextCursor,
                nextIdAfter,
                slice.getSize(),
                totalElements,
                slice.hasNext()
        );
    }
}
