package tn.esprit.pfe.approbation.Repositories;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.pfe.approbation.Entities.LeaveRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    Optional<LeaveRequest> findById(Long id);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.endDate = :today " +
            "AND lr.managerApproved = true AND lr.rhApproved = true " +
            "AND lr.id IN (SELECT MAX(lr2.id) FROM LeaveRequest lr2 WHERE lr2.user.id = lr.user.id GROUP BY lr2.user.id)")
    List<LeaveRequest> findApprovedLeaveRequestsEndingToday(@Param("today") LocalDate today);

}
