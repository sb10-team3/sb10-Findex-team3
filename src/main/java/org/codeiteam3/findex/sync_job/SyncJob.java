package org.codeiteam3.findex.sync_job;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "sync_jobs")
public class SyncJob {
    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id", nullable = false)
    private IndexInfo indexInfoId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", length = 10)
    @Size(max = 10)
    private JobType jobType;

    @NotNull
    @Column(name = "target_date")
    private Instant targetDate;

    @Size(max = 15)
    @NotNull
    @Column(name = "worker", length = 15)
    private String worker;

    @NotNull
    @Column(name = "job_time")
    private Instant jobTime;

    @Size(max = 7)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 7)
    private Result result;
}
