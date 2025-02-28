package tn.esprit.pfe.approbation.delegate;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import tn.esprit.pfe.approbation.entities.User;
import tn.esprit.pfe.approbation.repositories.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
public class UpdateAutorisation implements JavaDelegate {
    private final UserRepository userRepository;

    public UpdateAutorisation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String userId = (String) delegateExecution.getVariable("userId");
        LocalDateTime startDateTime = (LocalDateTime) delegateExecution.getVariable("startDate");
        LocalDateTime endDateTime = (LocalDateTime) delegateExecution.getVariable("endDate");
        long hoursRequested = (ChronoUnit.HOURS.between(startDateTime, endDateTime));
        User user = userRepository.findByMatricule(userId);
        user.setSoldeAutorisation((int) (user.getSoldeAutorisation() - hoursRequested));
        user.setOccurAutorisation(user.getOccurAutorisation() -1);
    }
}
