package ua.idzo.resource.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "resource")
public class ResourceEntity extends BaseIntegerAutoIncrementEntity {

    @Column(nullable = false)
    private byte[] data;
}
