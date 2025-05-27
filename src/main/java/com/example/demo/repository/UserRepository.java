package com.example.demo.repository;

import com.example.demo.model.RoleType;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository

public  interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT DISTINCT u.username FROM User u WHERE :role MEMBER OF u.roles")
    List<String> findUsernamesByRole(@Param("role") RoleType role);
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r = :role")
    List<User> findByRolesContaining(@Param("role") RoleType role);

    @Query("SELECT u FROM User u WHERE :role MEMBER OF u.roles")
    List<User> findByRole(@Param("role") RoleType role);
}
