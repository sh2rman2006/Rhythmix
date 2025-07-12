package com.rhythmix.coreservice.mapper;

import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface EntitiesMapper<ENTITY, DTO> {

    DTO toDto(@NotNull ENTITY entity);

    ENTITY toEntity(@NotNull DTO dto);

    default List<DTO> toDtoList(Collection<ENTITY> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream().map(this::toDto).toList();
    }

    default List<ENTITY> toEntityList(Collection<DTO> dtos) {
        if (dtos == null) return Collections.emptyList();
        return dtos.stream().map(this::toEntity).toList();
    }

    default Set<DTO> toDtoSet(Collection<ENTITY> entities) {
        if (entities == null) return Collections.emptySet();
        return entities.stream().map(this::toDto).collect(Collectors.toSet());
    }

    default Set<ENTITY> toEntitySet(Collection<DTO> dtos) {
        if (dtos == null) return Collections.emptySet();
        return dtos.stream().map(this::toEntity).collect(Collectors.toSet());
    }

}
