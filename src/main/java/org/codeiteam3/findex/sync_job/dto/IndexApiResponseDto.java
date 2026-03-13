package org.codeiteam3.findex.sync_job.dto;

import java.util.List;

public record IndexApiResponseDto(
    Response response
) {
    public record Response(
            Header header,
            Body body
    ){}

    public record Header(
            String resultCode,
            String resultMsg
    ) {}

    public record Body(
            int numOfRows,
            int pageNo,
            int totalCount,
            Items items
    ) {}

    public record Items(
            List<IndexApiResponseItemDto> item
    ) {}
}
