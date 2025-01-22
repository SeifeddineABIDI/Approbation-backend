package tn.esprit.pfe.approbation.dtos;

import lombok.Data;
import tn.esprit.pfe.approbation.entities.User;

@Data
public class UserDto {
    private Integer id;
    private String matricule;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private double soldeConge;
    private String managerMatricule;

    // Converts User to UserDto
    public static UserDto fromEntity(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setMatricule(user.getMatricule());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setSoldeConge(user.getSoldeConge());
        if (user.getManager() != null) {
            dto.setManagerMatricule(user.getManager().getMatricule());
        }
        return dto;
    }
}