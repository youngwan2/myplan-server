package com.myplan.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskDTO {

    @NotNull(message = "제목은 비어있으면 안 됩니다.")
    @Size(min =2, max = 20, message = "제목은 최소 2자 이상 최대 20자 이하이어야 합니다.")
    private String title;

    private String description;
    private String colorCode;

    @NotNull(message = "시작시간은 비어 있으면 안 됩니다.")
    private LocalTime startTime;
    @NotNull(message = "종료시간은 비어 있으면 안 됩니다.")
    private LocalTime endTime;

    @Override
    public String toString() {
        return "TaskDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", colorCode='" + colorCode + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
