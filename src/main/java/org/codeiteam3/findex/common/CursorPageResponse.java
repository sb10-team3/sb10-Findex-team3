package org.codeiteam3.findex.common;

import java.util.List;
import java.util.UUID;

public record CursorPageResponse<T>(
        List<T> content,
        String nextCursor, // 다음 페이지 커서
        UUID nextIdAfter, // 마지막 요소 ID
        int size, // 페이지 크기
        Long totalElements, // 총 요소 수
        boolean hasNext // 다음 페이지 여부
) {
}
