package tn.esprit.pfe.approbation.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String icon;
    private String image;
    private String title;
    private String description;
    private String time;
    private String link;
    private boolean useRouter;
    private boolean isRead;
}