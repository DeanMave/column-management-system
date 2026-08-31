package io.github.deanmave.hplclims.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "column_usage_log")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ColumnUsageLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "column_id",nullable = false)
    private HplcColumn hplcColumn;
    @Column(name = "task_number",nullable = false)
    private String taskNumber;
    @Column(name = "drug_name",nullable = false)
    private String drugName;
    @Column(name = "analysis_parameters")
    private String analysisParameters;
    @Column(name = "storage_phase")
    private String storagePhase;
    @Column(name = "min_pressure")
    private Integer minPressure;
    @Column(name = "max_pressure")
    private Integer maxPressure;
    @Column(name = "start_date",nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "rejection_reason")
    private String rejectionReason;
}
