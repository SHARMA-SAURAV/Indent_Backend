package com.example.demo.controller;

import com.example.demo.model.IndentRequest;
import com.example.demo.model.IndentStatus;
import com.example.demo.model.User;
import com.example.demo.repository.IndentRequestRepository;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class IndentReminderScheduler {

    @Autowired
    private IndentRequestRepository indentRequestRepository;

    @Autowired
    private EmailService emailService; // custom service for sending mail

    @Scheduled(cron = "0 0 9 * * ?") // Daily at 9 AM
    public void checkAllPendingIndents() {
        LocalDateTime fourDaysAgo = LocalDateTime.now().minusDays(4);
        checkAndNotify(IndentStatus.PENDING_FLA, fourDaysAgo, "FLA");
        checkAndNotify(IndentStatus.PENDING_SLA, fourDaysAgo, "SLA");
        checkAndNotify(IndentStatus.PENDING_STORE, fourDaysAgo, "Store");
        checkAndNotify(IndentStatus.PENDING_FINANCE, fourDaysAgo, "Finance");
        checkAndNotify(IndentStatus.PENDING_PURCHASE, fourDaysAgo, "Purchase");
        checkAndNotify(IndentStatus.WAITING_FOR_USER_CONFIRMATION, fourDaysAgo, "User Reconfirmation");
        checkAndNotify(IndentStatus.PENDING_PURCHASE_GRC, fourDaysAgo, "Final Purchase");
        checkAndNotify(IndentStatus.PENDING_FINANCE_PAYMENT, fourDaysAgo, "Final Finance");
    }

    private void checkAndNotify(IndentStatus status, LocalDateTime cutoffDate, String roleLabel) {
        List<IndentRequest> indents = indentRequestRepository
                .findByStatusAndUpdatedAtBefore(status, cutoffDate); // use updatedAt if you track status changes

        for (IndentRequest indent : indents) {
            User recipient = switch (status) {
                case PENDING_FLA -> indent.getFla();
                case PENDING_SLA -> indent.getSla();
                case PENDING_STORE -> indent.getStore();
                case PENDING_FINANCE, PENDING_FINANCE_PAYMENT -> indent.getFinance();
                case PENDING_PURCHASE, PENDING_PURCHASE_GRC -> indent.getPurchase();
                case UNDER_INSPECTION, WAITING_FOR_USER_CONFIRMATION -> indent.getRequestedBy();
                default -> null;
            };

            if (recipient != null && recipient.getEmail() != null) {
                String subject = "Reminder: Indent Pending for Over 4 Days (" + roleLabel + ")";
                String body = String.format("""
                    Hello %s,

                    This is a reminder that the following indent is still pending at your level for more than 4 days:

                    Indent ID     : %d
                    Project       : %s
                    Requested By  : %s
                    Current Step  : %s
                    Created On    : %s

                    Please take the necessary action to move it forward.

                    Regards,
                    Indent Management System
                    """,
                        recipient.getName(),
                        indent.getId(),
                        indent.getProject().getProjectName(),
                        indent.getRequestedBy().getName(),
                        status.name(),
                        indent.getCreatedAt().toLocalDate()
                );

                emailService.sendEmail(recipient.getEmail(), subject, body);
            }
        }
    }

}

