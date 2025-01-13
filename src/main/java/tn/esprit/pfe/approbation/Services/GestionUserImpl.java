package tn.esprit.pfe.approbation.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pfe.approbation.Entities.User;
import tn.esprit.pfe.approbation.Repositories.UserRepository;

import java.util.List;

@Service
public class GestionUserImpl implements IGestionUser {

    @Autowired
    UserRepository userRepository;


    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

}
