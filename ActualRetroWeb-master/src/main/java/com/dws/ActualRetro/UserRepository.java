package com.dws.ActualRetro;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Optional;

@Component
public interface UserRepository extends JpaRepository<Users, Long> {
    Users findById(long id);
    Optional<Users> findByName(String name);
    Boolean existsByName(String name);
    Boolean existsByMail(String mail);
    @Modifying
    @Transactional
    @Query(value = "SET FOREIGN_KEY_CHECKS = 0", nativeQuery = true)
    void disableForeignKeyChecks();
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM users WHERE ID=:id", nativeQuery = true)
    void removeUser(@Param("id") long id);
    @Modifying
    @Transactional
    @Query(value = "SET FOREIGN_KEY_CHECKS = 1", nativeQuery = true)
    void reenableForeignKeyChecks();
}
