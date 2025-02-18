package tn.esprit.pfe.approbation.services;

import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pfe.approbation.dtos.ManagerDto;
import tn.esprit.pfe.approbation.dtos.UserDto;
import tn.esprit.pfe.approbation.entities.User;

import java.util.List;

public interface IGestionUser {
    public List<User>findAll();
    public User addUser(User user);
    public List<ManagerDto> getManagers();
    public List<UserDto> searchUsers(String firstName, String lastName, String email, String matricule);
    public UserDto updateUser(Integer userId, UserDto userDto, MultipartFile imageFile);
    public void deleteUser(Integer userId);
    public List<User> getUsersByManager(User manager);
    }
