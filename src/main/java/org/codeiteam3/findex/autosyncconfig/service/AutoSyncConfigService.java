package org.codeiteam3.findex.autosyncconfig.service;

import lombok.RequiredArgsConstructor;
import org.codeiteam3.findex.autosyncconfig.entity.AutoSyncConfig;
import org.codeiteam3.findex.autosyncconfig.dto.AutoSyncConfigResponseDto;
import org.codeiteam3.findex.autosyncconfig.dto.AutoSyncConfigUpdateRequestDto;
import org.codeiteam3.findex.autosyncconfig.mapper.AutoSyncConfigMapper;
import org.codeiteam3.findex.autosyncconfig.repository.AutoSyncConfigRepository;
import org.codeiteam3.findex.pagination.CursorPageResponse;
import org.codeiteam3.findex.pagination.CursorPageResponseMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AutoSyncConfigService {
    private final AutoSyncConfigRepository autoSyncConfigRepository;
    private final AutoSyncConfigMapper autoSyncConfigMapper;
    private final CursorPageResponseMapper cursorPageResponseMapper;

    @Transactional
    public AutoSyncConfigResponseDto update(UUID id, AutoSyncConfigUpdateRequestDto dto) {
        AutoSyncConfig config = autoSyncConfigRepository.findByIdWithIndexInfo(id)
                .orElseThrow(NoSuchElementException::new);

        config.update(dto.enabled());

        return autoSyncConfigMapper.toDto(autoSyncConfigRepository.save(config));
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<AutoSyncConfigResponseDto> findAll(
            UUID indexInfoId,
            Boolean enabled,
            UUID idAfter,
            String cursor,
            String sortField,
            String sortDirection,
            int size
    ){
        //validation
        if (size < 1) {
            throw new IllegalArgumentException("페이지 크기(size)는 1 이상이어야 합니다.");
        }

        String normalizedCursor = (cursor == null || cursor.isBlank()) ? null : cursor;

        Set<String> allowField = Set.of("indexInfo.indexName");

        if (!allowField.contains(sortField)) {
            throw new IllegalArgumentException("적합하지 않은 정렬필드(sortField)입니다.");
        }

        String normalizedSortField = sortField;

        Sort.Direction normalizedDirection =
                sortDirection.equalsIgnoreCase("asc")
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(
                0,
                size,
                Sort.by(normalizedDirection, normalizedSortField)
                        .and(Sort.by(normalizedDirection, "id"))
        );

        Long totalElements = autoSyncConfigRepository.countElements(indexInfoId, enabled);

        Slice<AutoSyncConfigResponseDto> slice =
                findAutoSyncConfigSlice(
                        indexInfoId,
                        enabled,
                        idAfter,
                        normalizedCursor,
                        normalizedSortField,
                        normalizedDirection,
                        pageable
                ).map(autoSyncConfigMapper::toDto);

        AutoSyncConfigResponseDto last =
                !slice.getContent().isEmpty()
                        ? slice.getContent().get(slice.getNumberOfElements() - 1)
                        : null;

        String nextCursor = null;
        UUID nextIdAfter = null;

        if (slice.hasNext()) {
            nextCursor = findNextCursor(last, normalizedSortField);
            nextIdAfter = last.id();
        }

        return cursorPageResponseMapper.fromSlice(
                slice,
                nextCursor,
                nextIdAfter,
                totalElements
        );

    }

    private Slice<AutoSyncConfig> findAutoSyncConfigSlice(
            UUID indexInfoId,
            Boolean enabled,
            UUID idAfter,
            String cursor,
            String sortField,
            Sort.Direction direction,
            Pageable pageable
    ){

        if (cursor == null) {
            return autoSyncConfigRepository.findAllByIndexInfoFirstPage(
                    indexInfoId, enabled, pageable
            );
        }
        if (direction.isDescending()) {
            return autoSyncConfigRepository.findAllByIndexInfoNextPageDesc(
                    indexInfoId, enabled, idAfter, cursor, pageable
            );
        } else {
            return autoSyncConfigRepository.findAllByIndexInfoNextPageAsc(
                    indexInfoId, enabled, idAfter, cursor, pageable
            );
        }
    }


    private Boolean parseBooleanCursor(String cursor){
        if(cursor == null) return null;
        return Boolean.parseBoolean(cursor);
    }

    private String findNextCursor(AutoSyncConfigResponseDto last, String sortField){

        return switch(sortField){

            case "indexInfo.indexName" -> last.indexName();

            case "enabled" -> String.valueOf(last.enabled());

            default -> throw new IllegalArgumentException("제대로 되지 않은 sortField 입니다.");
        };
    }
}
