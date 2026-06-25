package com.example.store.repository;

import com.example.store.entity.Order;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(
            """
        SELECT o FROM Order o
        WHERE (:cursor IS NULL OR o.id > :cursor)
        ORDER BY o.id ASC
    """)
    public List<Order> fetchNextPage(@Param("cursor") Long cursor, Pageable pageable);
}
