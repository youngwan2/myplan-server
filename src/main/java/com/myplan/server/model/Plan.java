package com.myplan.server.model;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class) // 계정생성 및 수정 날짜 자동 관리 위해
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate planDate;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "Plan{" +
                "id=" + id +
                ", planDate=" + planDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", member=" + member +
                '}';
    }

    @ManyToOne
    @JoinColumn(name = "member_id") // Member 테이블의 id 컬럼만 결합
    private  Member member;
}
