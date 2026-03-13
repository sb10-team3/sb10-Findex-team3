package org.codeiteam3.findex.indexinfo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.codeiteam3.findex.enums.SourceType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor //기본 생성자
@Getter
@Entity
@Table(name = "index_infos",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"index_classification", "index_name"})
        })
@EntityListeners(AuditingEntityListener.class)
public class IndexInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "index_classification", nullable = false, updatable = false, length = 100)
    private String indexClassification;

    @Column(name = "index_name", nullable = false, updatable = false, length = 50)
    private String indexName;

    @Column(name = "employed_items_count", nullable = false)
    private Integer employedItemsCount;

    @Column(name = "base_point_in_time", nullable = false)
    private LocalDate basePointInTime;

    @Column(name = "base_index", nullable = false, precision = 18, scale = 2)
    private BigDecimal baseIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false,updatable = false, length = 20)
    private SourceType sourceType;

    @Column(name = "favorite", nullable = false)
    private Boolean favorite;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;

    public IndexInfo(String indexClassification, String indexName, Integer employedItemsCount, LocalDate basePointInTime, BigDecimal baseIndex, SourceType sourceType, Boolean favorite) {
        this.indexClassification = indexClassification;
        this.indexName = indexName;
        this.employedItemsCount = employedItemsCount;
        this.basePointInTime = basePointInTime;
        this.baseIndex = baseIndex;
        this.sourceType = sourceType;
        this.favorite = favorite;
    }
}
