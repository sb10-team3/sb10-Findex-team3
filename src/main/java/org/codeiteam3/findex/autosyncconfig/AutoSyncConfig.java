package org.codeiteam3.findex.autosyncconfig;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.codeiteam3.findex.indexinfo.IndexInfo;

import java.util.UUID;

@Entity
@Table(name = "auto_sync_configs")
@NoArgsConstructor
@Getter
public class AutoSyncConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id", nullable = false, unique = true)
    private IndexInfo indexInfo;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;
}
