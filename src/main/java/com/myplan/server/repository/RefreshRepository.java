package com.myplan.server.repository;

import com.myplan.server.model.Refresh;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {
    Boolean  existsByRefresh(String refresh);

    Refresh findOneByUsername(String username);

    @Transactional
    void deleteAllByUsername(String username);

    @Transactional
    void deleteByRefresh(String refresh);

}
