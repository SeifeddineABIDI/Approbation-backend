package tn.esprit.pfe.approbation.delegate;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring6.SpringTemplateEngine;
import tn.esprit.pfe.approbation.entities.User;
import tn.esprit.pfe.approbation.repositories.UserRepository;
import org.thymeleaf.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.pfe.approbation.services.LeaveService;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Transactional
@Component
public class UpdateConge implements JavaDelegate {
    private static final Logger logger = LoggerFactory.getLogger(UpdateConge.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSenderImpl mailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String userId = (String) execution.getVariable("userId");
        LocalDateTime startDate = (LocalDateTime) execution.getVariable("startDate");
        LocalDateTime endDate = (LocalDateTime) execution.getVariable("endDate");
        Boolean goAfterMidday = (Boolean) execution.getVariable("goAfterMidday");
        Boolean backAfterMidday = (Boolean) execution.getVariable("backAfterMidday");
        boolean leaveApproved = (boolean) execution.getVariable("leaveApproved");
        String refusalComment = (String) execution.getVariable("refusalComment");

        User user = userRepository.findByMatricule(userId);
        if (user == null) {
            logger.error("User not found for matricule: {}", userId);
            throw new Exception("User not found");
        }

        logger.info("[UpdateConge] Before update: userId={}, soldeConge={}", userId, user.getSoldeConge());
        // Calculate working days excluding Saturdays and Sundays, then subtract 0.5 for goAfterMidday and/or backAfterMidday if true
        double workingDays = LeaveService.calculateWorkingDays(startDate, endDate, goAfterMidday, backAfterMidday);

        if (leaveApproved) {
            user.setSoldeConge(user.getSoldeConge() - workingDays);
            userRepository.save(user);
            logger.info("[UpdateConge] After update: userId={}, soldeConge={}", userId, user.getSoldeConge());
            sendEmail(user.getEmail(), user.getFirstName(), "approved", "Your leave request has been approved.");
        }
        if (!leaveApproved && refusalComment != null && !refusalComment.isEmpty()) {
            logger.info("[UpdateConge] Leave refused for userId={}, comment={}", userId, refusalComment);
            sendEmail(user.getEmail(), user.getFirstName(), "rejected", "Reason: " + refusalComment);
        }

        execution.setVariable("leaveApproved", leaveApproved);
    }

    private double calculateWorkingDays(LocalDateTime startDate, LocalDateTime endDate, Boolean goAfterMidday, Boolean backAfterMidday) {
        double totalDays = 0.0;
        LocalDateTime currentDate = startDate.toLocalDate().atStartOfDay();
        LocalDateTime endDateAtStart = endDate.toLocalDate().atStartOfDay();
        boolean isFirstDay = true;
        boolean isLastDay = false;

        while (!currentDate.isAfter(endDateAtStart)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                isLastDay = currentDate.toLocalDate().equals(endDate.toLocalDate());
                if (isFirstDay && isLastDay) {
                    // Leave starts and ends on the same day
                    if (Boolean.TRUE.equals(goAfterMidday) && Boolean.TRUE.equals(backAfterMidday)) {
                        totalDays += 1.0; // Depart and return after midday: full day
                    } else if (Boolean.TRUE.equals(goAfterMidday) || Boolean.TRUE.equals(backAfterMidday)) {
                        totalDays += 0.5; // Either depart or return after midday: half-day
                    } else {
                        totalDays += 1.0; // Full day
                    }
                } else if (isFirstDay) {
                    if (Boolean.TRUE.equals(goAfterMidday)) {
                        totalDays += 0.5; // Depart after midday: half-day
                    } else {
                        totalDays += 1.0; // Full day
                    }
                } else if (isLastDay) {
                    if (Boolean.TRUE.equals(backAfterMidday)) {
                        totalDays += 1.0; // Return after midday: full day
                    } else {
                        totalDays += 0.5; // Return before midday: half-day
                    }
                } else {
                    totalDays += 1.0; // Full day
                }
            }
            isFirstDay = false;
            currentDate = currentDate.plusDays(1);
        }
        return totalDays;
    }

    public void sendEmail(String to, String userName, String status, String message) throws MessagingException {
        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("status", status);
        context.setVariable("message", message);
        String htmlContent = templateEngine.process("leaveRequestNotification", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(to);
        helper.setSubject("Leave Request Status");
        helper.setText(htmlContent, true);
        mailSender.send(mimeMessage);
    }
}