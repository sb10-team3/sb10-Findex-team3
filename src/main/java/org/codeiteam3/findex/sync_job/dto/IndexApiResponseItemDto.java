package org.codeiteam3.findex.sync_job.dto;

public record IndexApiResponseItemDto(
        String basDt,            // 기준일자 info
        String idxNm,            // 지수명 info
        String idxCsf,           // 지수 분류 info
        String epyItmsCnt,       // 편입 종목 수 info
        String clpr,             // 종가
        String vs,               // 전일 대비
        String fltRt,            // 등락률
        String mkp,              // 시가
        String hipr,             // 고가
        String lopr,             // 저가
        String trqu,             // 거래량
        String trPrc,            // 거래대금
        String lstgMrktTotAmt,   // 상장 시가총액
        String lsYrEdVsFltRg,    // 연초 대비 등락폭
        String lsYrEdVsFltRt,    // 연초 대비 등락률
        String yrWRcrdHgst,      // 연중 최고가
        String yrWRcrdHgstDt,    // 연중 최고가 날짜
        String yrWRcrdLwst,      // 연중 최저가
        String yrWRcrdLwstDt,    // 연중 최저가 날짜
        String basPntm,          // 기준 시점
        String basIdx            // 기준 지수 info
) {
}
