package com.rhythmix.coreservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "entity_likes", schema = "public", indexes = {
        @Index(name = "idx_entity_likes_entity", columnList = "entity_type, entity_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_entity_like", columnNames = {"entity_type", "entity_id", "user_id"})
})
public class EntityLike {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 50)
    @NotNull
    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private RhythmixUser user;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;

}