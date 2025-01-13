package tn.esprit.pfe.approbation.Delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tn.esprit.pfe.approbation.Entities.User;
import tn.esprit.pfe.approbation.Repositories.UserRepository;

@Component
public class UpdateConge implements JavaDelegate {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Fetch the user ID, number of days requested, approval status, and refusal comment from process variables
        String userId = (String) execution.getVariable("userId");
        Long daysRequestedLong = (Long) execution.getVariable("daysRequested");
        int daysRequested = daysRequestedLong.intValue();
        boolean leaveApproved = (boolean) execution.getVariable("leaveApproved");
        String refusalComment = (String) execution.getVariable("refusalComment");

        // Retrieve the user from the database
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));

        // If leave is approved, update the user's leave balance
        if (leaveApproved) {
            // Deduct the requested days from the user's balance
            user.setSoldeConge(user.getSoldeConge() - daysRequested);
            user.setOnLeave(true);
            userRepository.save(user);
        }

        // Optionally, you can handle refusal comment, if leave is not approved
        if (!leaveApproved && refusalComment != null && !refusalComment.isEmpty()) {
            // Handle the refusal comment (you can store or log it as needed)
            // For now, let's just print it out (or you can save it somewhere).
            System.out.println("Refusal comment: " + refusalComment);
        }

        // Optionally, you can set process variables to indicate the outcome
        execution.setVariable("leaveApproved", leaveApproved);
    }
}
