package com.wafflestudio.msns.domain.model

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.validation.constraints.Size

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseTimeEntity(
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    open val id: UUID = UUID.randomUUID(),

    @CreatedDate
    @Column(name = "created_at")
    open var createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedBy
    @Column(name = "updated_at")
    open var updatedAt: LocalDateTime? = LocalDateTime.now(),
)
