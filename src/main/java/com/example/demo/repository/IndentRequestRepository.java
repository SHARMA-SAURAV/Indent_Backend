package com.example.demo.repository;

//package com.indentmanagement.repository;

//import com.indentmanagement.model.IndentRequest;
//import com.indentmanagement.model.IndentStatus;
import com.example.demo.model.IndentRequest;
import com.example.demo.model.IndentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IndentRequestRepository extends JpaRepository<IndentRequest, Long> {
    List<IndentRequest> findByStatus(IndentStatus status);
    // IndentRepository.java

        // Use JOIN FETCH to avoid LazyInitializationException
//        @Query("SELECT i FROM IndentRequest i JOIN FETCH i.requestedBy WHERE i.requestedBy.id = :userId")
//        List<IndentRequest> findByRequestedById(@Param("userId") Long userId);
    List<IndentRequest> findByRequestedById(Long userId);
    List<IndentRequest> findByFlaId(Long flaId);
    List<IndentRequest> findBySlaId(Long slaId);
}

