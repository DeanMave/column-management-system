package io.github.deanmave.hplclims.service.interfaces;

import io.github.deanmave.hplclims.domain.ColumnStatus;
import io.github.deanmave.hplclims.domain.HplcColumn;

import java.util.List;

public interface ColumnService {
    HplcColumn create(HplcColumn hplcColumn);

    List<HplcColumn> getAll();

    HplcColumn getById(Long id);

    void deleteById(Long id);

    HplcColumn changeStatus(Long id, ColumnStatus newStatus);

    HplcColumn correctData(Long id, HplcColumn newColumn);
}
