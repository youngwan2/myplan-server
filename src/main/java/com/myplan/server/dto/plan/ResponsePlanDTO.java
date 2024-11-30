package com.myplan.server.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResponsePlanDTO {
    private Long id;
    private LocalDate planDate;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}
