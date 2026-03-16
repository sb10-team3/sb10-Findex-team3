package org.codeiteam3.findex.syncjob.dto;

public record IndexApiResponseItemDto(
        String basDt,            // 기준일자 info data
        String idxNm,            // 지수명 info
        String idxCsf,           // 지수 분류 info
        String epyItmsCnt,       // 편입 종목 수 info
        String clpr,             // 종가 data
        String vs,               // 전일 대비 data
        String fltRt,            // 등락률 data
        String mkp,              // 시가 data
        String hipr,             // 고가 data
        String lopr,             // 저가 data
        String trqu,             // 거래량 data
        String trPrc,            // 거래대금 data
        String lstgMrktTotAmt,   // 상장 시가총액 data
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
