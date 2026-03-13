package org.codeiteam3.findex.sync_job.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.codeiteam3.findex.enums.JobType;
import org.codeiteam3.findex.enums.Result;
import org.codeiteam3.findex.indexinfo.entity.IndexInfo;

import java.time.LocalDate;
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

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "worker", length = 15, nullable = false)
    private String worker;

    @Column(name = "job_time", nullable = false)
    private LocalDate jobTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", length = 7, nullable = false)
    private Result result;

    public SyncJob(IndexInfo indexInfo, JobType jobType, LocalDate targetDate, String worker, LocalDate jobTime, Result result) {
        this.indexInfo = indexInfo;
        this.jobType = jobType;
        this.targetDate = targetDate;
        this.worker = worker;
        this.jobTime = jobTime;
        this.result = result;
    }
}
