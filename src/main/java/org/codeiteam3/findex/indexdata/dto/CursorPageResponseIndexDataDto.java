package org.codeiteam3.findex.indexdata.dto;

import org.codeiteam3.findex.common.CursorPageResponse;

public record CursorPageResponseIndexDataDto(
        CursorPageResponse<IndexDataDto> cursorPageResponse
) {
}
