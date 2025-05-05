package com.example.demo.repository;

//package com.indentmanagement.repository;

//import com.indentmanagement.model.IndentRequest;
//import com.indentmanagement.model.IndentStatus;
import com.example.demo.model.IndentRequest;
import com.example.demo.model.IndentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IndentRequestRepository extends JpaRepository<IndentRequest, Long> {
    List<IndentRequest> findByStatus(IndentStatus status);
    List<IndentRequest> findByRequestedById(Long userId);
    List<IndentRequest> findByFlaId(Long flaId);
    List<IndentRequest> findBySlaId(Long slaId);
}

