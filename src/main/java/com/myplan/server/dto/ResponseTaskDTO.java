package com.myplan.server.dto;

import lombok.*;

import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseTaskDTO {

    private Long id;
    private String title;
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
    private String colorCode;

    @Override
    public String toString() {
        return "ResponseTaskDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", colorCode='" + colorCode + '\'' +
                '}';
    }
}
