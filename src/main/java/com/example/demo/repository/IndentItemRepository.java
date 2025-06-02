package com.example.demo.repository;

import com.example.demo.model.IndentItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndentItemRepository extends JpaRepository<IndentItem, Long> {
    List<IndentItem> findByIndentRequestId(Long indentRequestId);
}

