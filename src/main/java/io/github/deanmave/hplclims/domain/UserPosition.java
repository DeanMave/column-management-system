package io.github.deanmave.hplclims.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserPosition {
    CHEMIST_ANALYST("Химик-аналитик"),
    SENIOR_CHEMIST_ANALYST("Старший химик-аналитик"),
    PHARMACIST_ANALYST("Провизор-аналитик"),
    SENIOR_PHARMACIST_ANALYST("Старший провизор-аналитик"),
    HEAD_OF_ANALYTICAL_LAB("Руководитель аналитической лаборатории"),
    HEAD_OF_TESTING_LAB("Руководитель испытательной лаборатории"),
    METROLOGIST("Метролог"),
    QUALITY_DEPT_SPECIALIST("Сотрудник отдела качества"),
    DIRECTOR("Директор"),
    ORG_DEPT_SPECIALIST("Сотрудник орг. отдела");

    private final String title;
}
