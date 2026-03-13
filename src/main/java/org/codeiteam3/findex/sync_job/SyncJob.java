package org.codeiteam3.findex.sync_job;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.codeiteam3.findex.indexinfo.entity.IndexInfo;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "sync_jobs")
public class SyncJob {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id", nullable = false, unique = true)
    private IndexInfo indexInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", length = 10, nullable = false)
    private JobType jobType;

    @Column(name = "target_date", nullable = false)
    private Instant targetDate;

    @Column(name = "worker", length = 15, nullable = false)
    private String worker;

    @Column(name = "job_time", nullable = false)
    private Instant jobTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "", length = 7, nullable = false)
    private Result result;
}
