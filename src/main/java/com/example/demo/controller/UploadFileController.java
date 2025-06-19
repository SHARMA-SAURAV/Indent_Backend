package com.example.demo.controller;

import com.example.demo.model.IndentAttachment;
import com.example.demo.model.IndentRequest;
import com.example.demo.repository.IndentAttachmentRepository;
import com.example.demo.repository.IndentRequestRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/api/upload")
@Controller
public class UploadFileController {

    @Autowired
    private IndentAttachmentRepository indentAttachmentRepository;
    @Autowired
    private IndentRequestRepository indentRequestRepository;

    @PostMapping(value= "/{indentId}/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadAttachment(@PathVariable Long indentId,
                                              @RequestParam("file") MultipartFile file,
                                              @RequestParam("role") String role) throws IOException {

        IndentRequest indent = indentRequestRepository.findById(indentId)
                .orElseThrow(() -> new RuntimeException("Indent not found"));

        // Step 1: Save the uploaded file
        String uploadDir = "uploads/indent_attachments/";
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());


        // Save metadata if needed (optional)
        IndentAttachment attachment = new IndentAttachment();
        attachment.setIndentRequest(indent);
        attachment.setRole(role.toUpperCase());
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setUploadedAt(LocalDateTime.now());
        attachment.setFilePath(filePath.toString());
        indentAttachmentRepository.save(attachment);

        // Step 2: Merge into combined PDF
        String combinedDir = "uploads/combined/";
        String combinedFilePath = combinedDir + "combined_" + indentId + ".pdf";
        Files.createDirectories(Paths.get(combinedDir));
        indent.setCombinedPdfPath(combinedFilePath);

        Path combinedPath = Paths.get(combinedFilePath);

        if (!file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
            return ResponseEntity.badRequest().body("Only PDF files are supported for combining.");
        }

        if (!Files.exists(combinedPath)) {
            // First time: Just copy the uploaded PDF
            Files.copy(filePath, combinedPath);
        } else {
            // Append to existing combined PDF
            try (
                    PdfDocument existingPdf = new PdfDocument(new PdfReader(combinedFilePath), new PdfWriter(combinedFilePath + ".tmp"));
                    PdfDocument newPdf = new PdfDocument(new PdfReader(filePath.toString()))
            ) {
                PdfMerger merger = new PdfMerger(existingPdf);
                merger.merge(newPdf, 1, newPdf.getNumberOfPages());
                newPdf.close();
                existingPdf.close();

                // Replace original file
                Files.move(Paths.get(combinedFilePath + ".tmp"), combinedPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }

        return ResponseEntity.ok(Map.of(
                "message", "File uploaded and added to combined PDF",
                "combinedPdf", combinedFilePath
        ));
    }

}
