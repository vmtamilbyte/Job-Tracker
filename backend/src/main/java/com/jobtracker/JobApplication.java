package com.jobtracker;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class JobApplication {
    public enum Status { APPLIED, INTERVIEW, OFFER, REJECTED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String company;
    private String role;
    @Enumerated(EnumType.STRING)
    private Status status = Status.APPLIED;
    @Column(length = 10000)
    private String jobDescription;
    private Integer matchScore;
    @Column(length = 2000)
    private String notes;
    private LocalDate followUpOn;
    private LocalDate createdOn = LocalDate.now();
}
