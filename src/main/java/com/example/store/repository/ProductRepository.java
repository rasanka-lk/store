package com.example.store.repository;

import com.example.store.entity.Product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Override
    @EntityGraph(attributePaths = "orders")
    List<Product> findAll();

    @Override
    @EntityGraph(attributePaths = "orders")
    Optional<Product> findById(Long id);

    @Query(
            """
        SELECT p FROM Product p
        WHERE (:cursor IS NULL OR p.id > :cursor)
        ORDER BY p.id ASC
    """)
    public List<Product> fetchNextPage(@Param("cursor") Long cursor, Pageable pageable);
}
