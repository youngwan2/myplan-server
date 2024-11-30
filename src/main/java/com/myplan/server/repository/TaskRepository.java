package com.myplan.server.repository;

import com.myplan.server.dto.task.ResponseTaskDTO;
import com.myplan.server.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT new com.myplan.server.dto.task.ResponseTaskDTO(e.id, e.title, e.description, e.startTime, e.endTime, e.colorCode) " +
            "FROM Task e WHERE e.id = :taskId")
    ResponseTaskDTO findResponseTaskById(@Param("taskId") Long taskId);

    @Query("SELECT new com.myplan.server.dto.task.ResponseTaskDTO(e.id, e.title, e.description, e.startTime, e.endTime, e.colorCode) " +
            "FROM Task e WHERE e.id = :taskId")
    List<ResponseTaskDTO> findAllResponseTaskById(@Param("taskId") Long taskId);

    @Modifying
    @Query("UPDATE Task e SET e.title= :title, e.description = :description, e.colorCode = :colorCode  WHERE e.id = :taskId")
    int updateByPlanId(@Param("taskId") Long taskId,
                        @Param("title") String title,
                        @Param("description") String description,
                        @Param("colorCode") String colorCode);


    @Query("SELECT e FROM Task e WHERE e.plan.id = :planId")
    List<Task> findAllByPlanId(@Param("planId") Long planId);

    @Query("SELECT e FROM Task e " +
            "WHERE e.plan.id = :planId " +
            "AND (" +
            "(:startTime >= e.startTime AND :startTime <= e.endTime) " + // 시작 시간이 기존 범위 안에 포함
            "OR (:endTime >= e.startTime AND :endTime <= e.endTime) " +  // 종료 시간이 기존 범위 안에 포함
            "OR (e.startTime >= :startTime AND e.endTime <= :endTime))"  // 기존 범위가 요청 범위 안에 포함
    )
    List<Task> findByTimeBetweenAndId(
            @Param("planId") Long planId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);


}
