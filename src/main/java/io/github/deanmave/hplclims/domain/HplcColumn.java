package io.github.deanmave.hplclims.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "hplc_column")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class HplcColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String manufacturer;
    @Column(name = "serial_number",nullable = false)
    private String serialNumber;
    @Column(name = "part_number",nullable = false)
    private String partNumber;
    @Column(nullable = false)
    private Integer length;
    @Column(nullable = false)
    private Integer diameter;
    @Column(name = "particle_size",nullable = false)
    private BigDecimal particleSize;
    @Column(name = "installation_date",nullable = false)
    private LocalDate installationDate;
    @Column(name = "ph_min",nullable = false)
    private Integer phMin;
    @Column(name = "ph_max",nullable = false)
    private Integer phMax;
    @Column(name = "stationary_phase",nullable = false)
    private String stationaryPhase;
    @Column(name = "max_pressure",nullable = false)
    private Integer maxPressure;
    @Column(name = "owner_organization")
    private String ownerOrganization;
    @Column(name = "return_date")
    private LocalDate returnDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ColumnStatus status;
    @Column(name = "internal_code",nullable = false,unique = true)
    private String internalCode;

    public boolean isExternal(){
        return ownerOrganization != null;
    }

    public boolean isAvailable(){
        return status == ColumnStatus.AVAILABLE;
    }
}
