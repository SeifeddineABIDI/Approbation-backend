package tn.esprit.pfe.approbation.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tn.esprit.pfe.approbation.token.Token;

import java.util.Collection;
import java.util.List;


@Entity
@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String matricule;

    private String firstName;

    private String lastName;

    private String email;
    @JsonIgnore
    private String password;

    private String avatar;
    private double soldeConge=20;

    private boolean isOnLeave=false;
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Token> tokens;

    @ManyToOne
    @JoinColumn(name = "MANAGER_ID", referencedColumnName = "MATRICULE")
    @JsonProperty("manager")
    @JsonIgnoreProperties({"password", "soldeConge", "onLeave", "manager","leaveRequests"})
    private User manager;

    private Integer soldeAutorisation=2;
    private Integer occurAutorisation=2;

    public void setSoldeConge(double soldeConge) {
        this.soldeConge = soldeConge;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public boolean isOnLeave() {
        return isOnLeave;
    }

    public void setOnLeave(boolean onLeave) {
        isOnLeave = onLeave;
    }
    public String getAvatar() {
        return avatar;
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
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
}

