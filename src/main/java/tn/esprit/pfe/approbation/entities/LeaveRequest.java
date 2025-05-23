package tn.esprit.pfe.approbation.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "LEAVE_REQUEST")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime requestDate = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = true)
    private Boolean approved=null;

    @Column(name = "proc_inst_id",nullable = false)
    private String procInstId;

    private Boolean goAfterMidday=false;
    private Boolean backAfterMidday=false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties("leaveRequests")
    @JoinColumn(name = "type_id")
    private TypeConge type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}


