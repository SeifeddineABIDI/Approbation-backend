package tn.esprit.pfe.approbation.repositories;

import org.apache.ibatis.annotations.Param;
import org.camunda.feel.syntaxtree.In;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.pfe.approbation.entities.LeaveRequest;
import tn.esprit.pfe.approbation.entities.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    Optional<LeaveRequest> findById(Long id);
    List<LeaveRequest> findByUserMatriculeAndApprovedOrderByIdDesc(String userId, Boolean approved);
    List<LeaveRequest> findByUserMatriculeOrderByIdDesc(String userId);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.user.matricule = :matricule AND " +
            "(:startDate BETWEEN lr.startDate AND lr.endDate OR " +
            " :endDate BETWEEN lr.startDate AND lr.endDate OR " +
            " lr.startDate BETWEEN :startDate AND :endDate)")
    List<LeaveRequest> findOverlappingLeaveRequests(@Param("userId") String matricule,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);
    List<LeaveRequest> findAll();

    @Query("SELECT l FROM LeaveRequest l " +
            "JOIN l.user u " +  // Join the user entity
            "WHERE LOWER(CAST(l.id AS string)) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(CAST(l.requestDate AS string)) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(CAST(l.startDate AS string)) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(CAST(l.endDate AS string)) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(l.procInstId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(CAST(l.approved AS string)) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +  // Example to search by user's username
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.matricule) LIKE LOWER(CONCAT('%', :query, '%')) OR " +  // Example to search by user's first name
            "LOWER(u.manager.matricule) LIKE LOWER(CONCAT('%', :query, '%'))") // Example to search by user's last name
    Page<LeaveRequest> searchRequests(@Param("query") String query, Pageable pageable);


    LeaveRequest findByProcInstId(String processInstanceId);
    List<LeaveRequest> findByUserMatriculeAndStartDateBetween(String userMatricule, LocalDateTime start, LocalDateTime end);
    @Query("SELECT lr FROM LeaveRequest lr WHERE (LOWER(lr.user.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(lr.user.lastName) LIKE LOWER(CONCAT('%', :query, '%'))) AND lr.type.name = :type")
    Page<LeaveRequest> searchRequestsByQueryAndType(@Param("query") String query, @Param("type") String type, Pageable pageable);
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.type.name = :type")
    Page<LeaveRequest> findByTypeName(@Param("type") String type, Pageable pageable);
    List<LeaveRequest> findByUserIn(List<User> users);
    List<LeaveRequest> findByUserInAndApproved(List<User> users, Boolean approved);
    List<LeaveRequest> findByUserId(Integer userId);
    List<LeaveRequest> findByStartDateBetween(LocalDateTime start, LocalDateTime end);
    List<LeaveRequest> findByApproved(Boolean approved);
}
