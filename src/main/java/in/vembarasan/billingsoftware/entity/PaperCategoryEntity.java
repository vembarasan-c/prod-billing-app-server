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
@Table(name = "tbl_paper_categories", indexes = {
    @Index(name = "idx_paper_category_id", columnList = "categoryId", unique = true),
    @Index(name = "idx_paper_category_name", columnList = "name")
})
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaperCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String categoryId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean isActive;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Timestamp updatedAt;
}
