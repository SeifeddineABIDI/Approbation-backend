package tn.esprit.pfe.approbation.repositories;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.pfe.approbation.entities.LeaveRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    Optional<LeaveRequest> findById(Long id);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.endDate = :today " +
            "AND lr.managerApproved = true AND lr.rhApproved = true " +
            "AND lr.id IN (SELECT MAX(lr2.id) FROM LeaveRequest lr2 WHERE lr2.user.matricule = lr.user.matricule GROUP BY lr2.user.matricule)")
    List<LeaveRequest> findApprovedLeaveRequestsEndingToday(@Param("today") LocalDate today);

    List<LeaveRequest> findByUserMatriculeAndManagerApprovedTrueAndRhApprovedTrueOrderByIdDesc(String userId);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.user.matricule = :matricule AND " +
            "(:startDate BETWEEN lr.startDate AND lr.endDate OR " +
            " :endDate BETWEEN lr.startDate AND lr.endDate OR " +
            " lr.startDate BETWEEN :startDate AND :endDate)")
    List<LeaveRequest> findOverlappingLeaveRequests(@Param("userId") String matricule,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);
}
