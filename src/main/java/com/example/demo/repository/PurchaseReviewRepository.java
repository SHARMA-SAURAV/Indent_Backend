package com.example.demo.repository;

import com.example.demo.model.PurchaseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PurchaseReviewRepository extends JpaRepository<PurchaseReview, Long> {
    List<PurchaseReview> findByIndentRequestId(Long indentId);
}

