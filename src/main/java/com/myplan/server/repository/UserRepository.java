package com.myplan.server.repository;


import com.myplan.server.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<Member, Long> {

    Member findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @Query("select e.id from Member e where e.username = :username")
    Long findIdByUsername(@Param("username") String username);

}
