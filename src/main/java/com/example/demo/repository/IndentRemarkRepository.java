package com.example.demo.repository;

//package com.indentmanagement.repository;

//import com.indentmanagement.model.IndentRemark;
import com.example.demo.model.IndentRemark;
import com.example.demo.model.IndentRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndentRemarkRepository extends JpaRepository<IndentRemark, Long> {
    List<IndentRemark> findByIndentRequestId(Long indentRequestId);
}
