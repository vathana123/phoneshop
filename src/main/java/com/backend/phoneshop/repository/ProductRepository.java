package com.backend.phoneshop.repository;

import com.backend.phoneshop.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    @Modifying
    @Query("""
    UPDATE Product p
    SET p.availableUnit = p.availableUnit - :qty
    WHERE p.id = :id AND p.availableUnit >= :qty
""")
    int decreaseStock(@Param("id") Long id, @Param("qty") int qty);

    @Modifying
    @Query("""
    UPDATE Product p
    SET p.availableUnit = p.availableUnit + :qty
    WHERE p.id = :id
""")
    int increaseStock(@Param("id") Long id, @Param("qty") int qty);
}