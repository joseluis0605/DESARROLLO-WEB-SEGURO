package com.dws.ActualRetro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;


import javax.transaction.Transactional;
import java.util.List;

@Component
public interface ConsoleRepository extends JpaRepository<VDConsole, Long> {
    VDConsole findById(long id);
    @Query(value = "SELECT * FROM VDConsole WHERE price BETWEEN :pricemin AND :pricemax", nativeQuery = true)
    List<VDConsole> findConsoleBetweenPrices(@Param("pricemin") float pricemin, @Param("pricemax") float pricemax);
    @Query(value = "SELECT * FROM VDConsole WHERE maxcontrollers=:maxcon", nativeQuery = true)
    List<VDConsole> findConsoleWithControllers(@Param("maxcon") int maxcontrollers);
    @Modifying
    @Transactional
    @Query(value = "SET FOREIGN_KEY_CHECKS = 0", nativeQuery = true)
    void disableForeignKeyChecks();
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM vdconsole WHERE ID=:id", nativeQuery = true)
    void removeConsole(@Param("id") long id);
    @Modifying
    @Transactional
    @Query(value = "SET FOREIGN_KEY_CHECKS = 1", nativeQuery = true)
    void reenableForeignKeyChecks();
}
