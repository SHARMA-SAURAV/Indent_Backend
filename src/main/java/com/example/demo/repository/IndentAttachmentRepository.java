package com.example.demo.repository;

import com.example.demo.model.IndentAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndentAttachmentRepository extends JpaRepository<IndentAttachment, Long> {
    List<IndentAttachment> findByIndentRequest_Id(Long indentId);
}
