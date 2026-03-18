package org.codeiteam3.findex.autosyncconfig.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.codeiteam3.findex.indexinfo.entity.IndexInfo;

import java.util.UUID;

@Entity
@Table(name = "auto_sync_configs")
@NoArgsConstructor
@Getter
public class AutoSyncConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id", nullable = false, unique = true)
    private IndexInfo indexInfo;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    public AutoSyncConfig(IndexInfo indexInfo, boolean enabled) {
        this.indexInfo = indexInfo;
        this.enabled = enabled;
    }

    public void update(boolean enabled) {
        this.enabled = enabled;
    }
}
