package com.myplan.server.repository;


import com.myplan.server.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Member, Long> {

    Member findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

}
