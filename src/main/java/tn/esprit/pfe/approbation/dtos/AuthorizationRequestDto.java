package tn.esprit.pfe.approbation.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationRequestDto {
    private String userId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;


}