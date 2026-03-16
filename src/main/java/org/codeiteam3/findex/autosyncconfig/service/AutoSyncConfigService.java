package org.codeiteam3.findex.autosyncconfig.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.autosyncconfig.AutoSyncConfig;
import org.codeiteam3.findex.autosyncconfig.dto.AutoSyncConfigResponseDto;
import org.codeiteam3.findex.autosyncconfig.dto.AutoSyncConfigUpdateRequestDto;
import org.codeiteam3.findex.autosyncconfig.mapper.AutoSyncConfigMapper;
import org.codeiteam3.findex.autosyncconfig.repository.AutoSyncConfigRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AutoSyncConfigService {
    private final AutoSyncConfigRepository autoSyncConfigRepository;
    private final AutoSyncConfigMapper autoSyncConfigMapper;

    @Transactional
    public AutoSyncConfigResponseDto update(UUID id, AutoSyncConfigUpdateRequestDto dto) {
        AutoSyncConfig config = autoSyncConfigRepository.findByIdWithIndexInfo(id)
                .orElseThrow(NoSuchElementException::new);

        config.update(dto.enabled());

        return autoSyncConfigMapper.toDto(autoSyncConfigRepository.save(config));
    }

}
