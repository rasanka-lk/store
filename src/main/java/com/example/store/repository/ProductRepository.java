package com.example.store.repository;

import com.example.store.entity.Product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByDescriptionIgnoreCase(String description);

    @Query(
            """
        SELECT p FROM Product p
        WHERE (:cursor IS NULL OR p.id > :cursor)
        ORDER BY p.id ASC
    """)
    public List<Product> fetchNextPage(@Param("cursor") Long cursor, Pageable pageable);
}
