package tn.esprit.pfe.approbation.services;

import tn.esprit.pfe.approbation.entities.User;

import java.util.List;

public interface IGestionUser {
    public List<User>findAll();
    public User addUser(User user);
}
