package com.cyber.entity;

import com.cyber.enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "projects")
@Where(clause = "is_deleted=false") //this will automatically be added to each ProjectRepository query - when query is executed, deleted data will not appear !!
public class Project extends BaseEntity{

    @Column(unique = true) //if same project code entered, will crash program - server side validation!!
    private String projectCode;

    private String projectName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User assignedManager;

    private LocalDate startDate;
    private LocalDate endDate;

    private String projectDetail;

    @Enumerated(EnumType.STRING)
    private Status projectStatus;
}
