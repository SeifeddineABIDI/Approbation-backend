package tn.esprit.pfe.approbation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.pfe.approbation.entities.TypeConge;

@Repository
public interface TypeCongeRepository extends JpaRepository<TypeConge, Integer> {
    TypeConge findByName(String name);
}
