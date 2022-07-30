package com.example.aiatest.model.entity.support;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class)
})
@MappedSuperclass
public abstract class BaseEntity {
    @CreationTimestamp
    OffsetDateTime createdDate;
    @UpdateTimestamp
    OffsetDateTime updatedDate;
    @Id
    @GeneratedValue
    private Long id;
}
