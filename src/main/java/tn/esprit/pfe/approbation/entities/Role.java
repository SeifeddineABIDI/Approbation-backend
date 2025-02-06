package tn.esprit.pfe.approbation.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static tn.esprit.pfe.approbation.entities.Permission.ADMIN_CREATE;
import static tn.esprit.pfe.approbation.entities.Permission.ADMIN_DELETE;
import static tn.esprit.pfe.approbation.entities.Permission.ADMIN_READ;
import static tn.esprit.pfe.approbation.entities.Permission.ADMIN_UPDATE;
import static tn.esprit.pfe.approbation.entities.Permission.MANAGER_CREATE;
import static tn.esprit.pfe.approbation.entities.Permission.MANAGER_DELETE;
import static tn.esprit.pfe.approbation.entities.Permission.MANAGER_READ;
import static tn.esprit.pfe.approbation.entities.Permission.MANAGER_UPDATE;

@RequiredArgsConstructor
public enum Role {

    USER(Collections.emptySet()),
    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE,
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE,
                    MANAGER_CREATE
            )
    ),
    MANAGER(
            Set.of(
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE,
                    MANAGER_CREATE
            )
    ),
    RH(
            Set.of(
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE,
                    MANAGER_CREATE
            )
    )
    ;

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
