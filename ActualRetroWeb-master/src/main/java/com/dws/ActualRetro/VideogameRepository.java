package com.dws.ActualRetro;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;


import javax.transaction.Transactional;
import java.util.List;

@Component
public interface VideogameRepository extends JpaRepository<Videogame, Long> {
    Videogame findById(long id);
    @Query(value = "SELECT * FROM Videogame WHERE price BETWEEN :pricemin AND :pricemax", nativeQuery = true)
    List<Videogame> findVideogameBetweenPrices(@Param("pricemin") float pricemin, @Param("pricemax") float pricemax);
    @Query(value = "SELECT * FROM Videogame WHERE pegi=:pegi", nativeQuery = true)
    List<Videogame> findGamesPegi(@Param("pegi") int pegi);
    @Modifying
    @Transactional
    @Query(value = "SET FOREIGN_KEY_CHECKS = 0", nativeQuery = true)
    void disableForeignKeyChecks();
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM videogame WHERE ID=:id", nativeQuery = true)
    void removeGame(@Param("id") long id);
    @Modifying
    @Transactional
    @Query(value = "SET FOREIGN_KEY_CHECKS = 1", nativeQuery = true)
    void reenableForeignKeyChecks();
}



