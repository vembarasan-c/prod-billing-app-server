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
@Table(name = "tbl_papers", indexes = {
    @Index(name = "idx_paper_id", columnList = "paperId", unique = true),
    @Index(name = "idx_paper_category_id", columnList = "paperCategoryId"),
    @Index(name = "idx_paper_group_id", columnList = "paperGroupId")
})
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaperEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String paperId;

    @Column(nullable = false)
    private String name;

    // Denormalized category reference — no FK join for maximum read performance
    @Column(nullable = false)
    private String paperCategory;

    @Column(nullable = false)
    private String paperCategoryId;

    // Denormalized group reference — no FK join for maximum read performance
    @Column(nullable = false)
    private String paperGroup;

    @Column(nullable = false)
    private String paperGroupId;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long readingCount;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean isActive;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Timestamp updatedAt;
}
