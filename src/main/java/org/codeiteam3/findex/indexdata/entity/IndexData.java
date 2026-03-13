package org.codeiteam3.findex.indexdata.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.codeiteam3.findex.enums.SourceType;
import org.codeiteam3.findex.indexinfo.entity.IndexInfo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@Table(
        name = "index_datas",
        uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_index_info_id_base_date",
                columnNames = {"index_info_id", "base_date"}
        )
    }
)
public class IndexData {
    // 식별 id
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // 지수 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id", nullable = false, updatable = false)
    private IndexInfo indexInfo;

    // 날짜
    @Column(name = "base_date", nullable = false, updatable = false)
    private LocalDate baseDate;

    // 소스 타입
    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 20)
    private SourceType sourceType;

    // 시가
    @Column(name = "market_price", precision = 18, scale = 2, nullable = false)
    private BigDecimal marketPrice;

    // 종가
    @Column(name = "closing_price", precision = 18, scale = 2, nullable = false)
    private BigDecimal closingPrice;

    // 고가
    @Column(name = "high_price", precision = 18, scale = 2, nullable = false)
    private BigDecimal highPrice;

    // 저가
    @Column(name = "low_price", precision = 18, scale = 2, nullable = false)
    private BigDecimal lowPrice;

    // 대비
    @Column(name = "versus", precision = 18, scale = 2, nullable = false)
    private BigDecimal versus;

    // 등략율
    @Column(name = "fluctuation_rate", precision = 10, scale = 2, nullable = false)
    private BigDecimal fluctuationRate;

    // 거래량
    @Column(name = "trading_quantity", nullable = false)
    private Long tradingQuantity;

    // 거래대금
    @Column(name = "trading_price", nullable = false)
    private Long tradingPrice;

    // 상장시가총액
    @Column(name = "market_total_amount", nullable = false)
    private Long marketTotalAmount;

    public IndexData(
            IndexInfo indexInfo,
            LocalDate baseDate,
            SourceType sourceType,
            BigDecimal marketPrice,
            BigDecimal closingPrice,
            BigDecimal highPrice,
            BigDecimal lowPrice,
            BigDecimal versus,
            BigDecimal fluctuationRate,
            Long tradingQuantity,
            Long tradingPrice,
            Long marketTotalAmount
    ) {
        this.indexInfo = indexInfo;
        this.baseDate = baseDate;
        this.sourceType = sourceType;
        this.marketPrice = marketPrice;
        this.closingPrice = closingPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.versus = versus;
        this.fluctuationRate = fluctuationRate;
        this.tradingQuantity = tradingQuantity;
        this.tradingPrice = tradingPrice;
        this.marketTotalAmount = marketTotalAmount;
    }
}
