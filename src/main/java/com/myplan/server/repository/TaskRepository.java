package com.myplan.server.repository;

import com.myplan.server.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository  extends JpaRepository<Task, Long> {


}
