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

@Component
public class UpdateConge implements JavaDelegate {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSenderImpl mailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String userId = (String) execution.getVariable("userId");
        Long daysRequestedLong = (Long) execution.getVariable("daysRequested");
        int daysRequested = daysRequestedLong.intValue();
        boolean leaveApproved = (boolean) execution.getVariable("leaveApproved");
        String refusalComment = (String) execution.getVariable("refusalComment");
        User user = userRepository.findByMatricule(userId);
        if (user == null) {
            throw new Exception("User not found");
        }

        if (leaveApproved) {
            user.setSoldeConge(user.getSoldeConge() - daysRequested);
            userRepository.save(user);

        }
        if (!leaveApproved && refusalComment != null && !refusalComment.isEmpty()) {
            System.out.println("Refusal comment: " + refusalComment);

        }
        execution.setVariable("leaveApproved", leaveApproved);

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
