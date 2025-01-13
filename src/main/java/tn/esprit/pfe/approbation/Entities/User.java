package tn.esprit.pfe.approbation.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "ACT_ID_USER")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "ID_")
    @JsonProperty("id")
    private String id;

    @Column(name = "FIRST_")
    @JsonProperty("firstName")
    private String firstName;

    @Column(name = "LAST_")
    @JsonProperty("lastName")
    private String lastName;

    @Column(name = "EMAIL_")
    @JsonProperty("email")
    private String email;

    @Column(name = "PWD_")
    @JsonProperty("password")
    private String password;

    @Column(name = "SOLDE_CONGE")
    @JsonProperty("soldeConge")
    private double soldeConge=10;

    @Column(name = "IS_ON_LEAVE")
    @JsonProperty("isOnLeave")
    private boolean isOnLeave;

    @ManyToOne
    @JoinColumn(name = "MANAGER_ID", referencedColumnName = "ID_")
    @JsonProperty("manager")
    private User manager;

    public double getSoldeConge() {
        return soldeConge;
    }
    public void setSoldeConge(double soldeConge) {
        this.soldeConge = soldeConge;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {}
    public String getId() {
        return id;
    }
    public boolean isOnLeave() {
        return isOnLeave;
    }

    public void setOnLeave(boolean onLeave) {
        isOnLeave = onLeave;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", soldeConge=" + soldeConge +
                ", isOnLeave=" + isOnLeave +
                ", manager=" + manager +
                '}';
    }
}

