package com.example.demo.repository;

import com.example.demo.model.IndentRequest;
import com.example.demo.model.IndentStatus;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IndentRequestRepository extends JpaRepository<IndentRequest, Long> {
   public  List<IndentRequest> findByStatus(IndentStatus status);

    List<IndentRequest> findByRequestedById(Long userId);
    List<IndentRequest> findByFlaId(Long flaId);
    List<IndentRequest> findBySlaId(Long slaId);
    List<IndentRequest> findByFlaAndStatus(User fla, IndentStatus status);
    List<IndentRequest> findBySlaAndStatus(User sla, IndentStatus status);

    List<IndentRequest> findByStatusAndAssignedToId(IndentStatus status, Long userId);
    List<IndentRequest> findByStatusAndStore_Id(IndentStatus status, Long storeId);
    List<IndentRequest> findByRequestedByIdAndStatus(Long userId, IndentStatus status);

    List<IndentRequest> findByRequestedBy(User user);
    @Query("SELECT ir FROM IndentRequest ir " +
            "LEFT JOIN FETCH ir.fla " +
            "LEFT JOIN FETCH ir.sla " +
            "WHERE ir.status = :status")
    List<IndentRequest> findByStatusWithUsers(@Param("status") IndentStatus status);

    List<IndentRequest> findByFla(User fla);
    List<IndentRequest> findBySla(User sla);
    List<IndentRequest> findByStore(User store);
    List<IndentRequest> findByFinance(User finance);
    List<IndentRequest> findByPurchase(User purchase);

    @Query("SELECT ir FROM IndentRequest ir JOIN FETCH ir.requestedBy")
    List<IndentRequest> findAllWithUser();

 // New methods for category-based operations
 List<IndentRequest> findByCategory(String category);

 List<IndentRequest> findByCategoryAndStatus(String category, IndentStatus status);

 @Query("SELECT ir FROM IndentRequest ir WHERE ir.category = :category AND ir.assignedTo.id = :userId")
 List<IndentRequest> findByCategoryAndAssignedUserId(@Param("category") String category, @Param("userId") Long userId);

 // Batch-related queries
 List<IndentRequest> findByBatchId(String batchId);

 @Query("SELECT ir FROM IndentRequest ir WHERE ir.batchId = :batchId ORDER BY ir.batchSequence")
 List<IndentRequest> findByBatchIdOrderBySequence(@Param("batchId") String batchId);

 // File-related queries
 List<IndentRequest> findByFileNameIsNotNull();

 @Query("SELECT ir FROM IndentRequest ir WHERE ir.fileName IS NOT NULL AND ir.category = :category")
 List<IndentRequest> findByCategoryWithFiles(@Param("category") String category);

 // Combined queries for role-based access
 @Query("SELECT ir FROM IndentRequest ir WHERE ir.category = :category AND ir.status IN :statuses")
 List<IndentRequest> findByCategoryAndStatusIn(@Param("category") String category, @Param("statuses") List<IndentStatus> statuses);

 @Query("SELECT DISTINCT ir.category FROM IndentRequest ir WHERE ir.category IS NOT NULL")
 List<String> findAllCategories();

}

