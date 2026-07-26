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
@Table(name = "machine_category", indexes = {
    @Index(name = "idx_machine_category_id", columnList = "categoryId", unique = true)
})
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MachineCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, updatable = false)
    private String categoryId;
    
    private String name;
    
    @Column(columnDefinition = "boolean default true")
    private Boolean isActive;
    
    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
    
    @UpdateTimestamp
    private Timestamp updatedAt;
}
