package io.github.deanmave.hplclims.repository;

import io.github.deanmave.hplclims.domain.ColumnStatus;
import io.github.deanmave.hplclims.domain.HplcColumn;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;


public interface ColumnRepository extends JpaRepository<HplcColumn, Long> {
    @Query(value = "SELECT nextval('column_int_code_seq')", nativeQuery = true)
    Long getNextIntSequenceValue();

    @Query(value = "SELECT nextval('column_ext_code_seq')", nativeQuery = true)
    Long getNextExtSequenceValue();

    @Query(value = """
        SELECT h FROM HplcColumn h
        WHERE (:isExternal IS NULL OR
              (:isExternal = true AND h.ownerOrganization IS NOT NULL) OR
              (:isExternal = false AND h.ownerOrganization IS NULL))
        AND (:ownerOrganization IS NULL OR
            LOWER(h.ownerOrganization) LIKE LOWER(CAST(:ownerOrganization AS string)))
        AND (:internalCode IS NULL OR
            LOWER(h.internalCode) LIKE LOWER(CAST(:internalCode AS string)))
        AND (:storageLocation IS NULL OR
            LOWER(h.storageLocation) LIKE LOWER(CAST(:storageLocation AS string)))
        AND (:status IS NULL OR h.status = :status)
        """)
    Page<HplcColumn> findFiltered(Boolean isExternal,
                                  String ownerOrganization,
                                  String internalCode,
                                  String storageLocation,
                                  ColumnStatus status,
                                  Pageable pageable);
}
