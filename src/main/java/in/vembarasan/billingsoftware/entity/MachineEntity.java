package in.vembarasan.billingsoftware.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "tbl_machines", indexes = {
    @Index(name = "idx_machine_id", columnList = "machineId", unique = true)
})
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MachineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, updatable = false)
    private String machineId;
    
    private String name;
    private String machineCategory;
    private String categoryId;
    private String reading;
    private String serialNumber;
    private String mobile;
    private String email;
    private String tonerRequestMobile;
    private String tonerRequestEmail;
    private String branchName;
    private String branchId;
    
    @Column(columnDefinition = "boolean default true")
    private Boolean isActive;
    
    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
    
    @UpdateTimestamp
    private Timestamp updatedAt;
}
