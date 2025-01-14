package tn.esprit.pfe.approbation.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pfe.approbation.entities.User;
import tn.esprit.pfe.approbation.repositories.UserRepository;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
public class GestionUserImpl implements IGestionUser {

    @Autowired
    UserRepository userRepository;


    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User addUser(User user) {
        if (user.getMatricule() == null || user.getMatricule().isEmpty()) {
            user.setMatricule(generateMatricule());
        }
        return userRepository.save(user);
        }
    private String generateMatricule() {
        int currentYearShort = Year.now().getValue() % 100;
        int currentMonth = LocalDate.now().getMonthValue();

        String lastMatricule = userRepository.findLastMatricule();
        System.out.println("Last matricule from database: " + lastMatricule);

        int nextSequence = 1;

        if (lastMatricule != null && !lastMatricule.isEmpty()) {
            try {
                if (lastMatricule.matches("^\\d{4}EMP\\d{3}$")) {
                    int yearFromLastMatricule = Integer.parseInt(lastMatricule.substring(0, 2));
                    int monthFromLastMatricule = Integer.parseInt(lastMatricule.substring(2, 4));
                    int sequenceFromLastMatricule = Integer.parseInt(lastMatricule.substring(7));

                    System.out.println("Parsed Year: " + yearFromLastMatricule + ", Month: " + monthFromLastMatricule + ", Sequence: " + sequenceFromLastMatricule);

                    if (yearFromLastMatricule == currentYearShort && monthFromLastMatricule == currentMonth) {
                        nextSequence = sequenceFromLastMatricule + 1;
                    } else {
                        System.out.println("Year or month mismatch. Resetting sequence.");
                    }
                } else {
                    System.out.println("Matricule format mismatch. Ignoring: " + lastMatricule);
                }
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                System.err.println("Error parsing last matricule: " + lastMatricule + ". Exception: " + e.getMessage());
            }
        } else {
            System.out.println("No matricule found. Starting from sequence 1.");
        }

        String generatedMatricule = String.format("%02d%02dEMP%03d", currentYearShort, currentMonth, nextSequence);
        System.out.println("Generated matricule: " + generatedMatricule);

        return generatedMatricule;
    }
}
