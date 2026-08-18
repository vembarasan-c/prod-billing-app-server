package in.vembarasan.billingsoftware.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "tbl_employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    private String email;

    private LocalDate dateOfJoin;

    private String branch;

    private String designation;

    private Double salary;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String photo;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String resume;

    @Column(nullable = false)
    @Builder.Default
    private String role = "EMPLOYEE";

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

}
