package com.samuel.enums;

public enum IMCClassification {
    UNDERWEIGHT("Abaixo do peso"),
    NORMAL_WEIGHT("Peso normal"),
    OVERWEIGHT("Sobrepeso"),
    OBESITY_GRADE_I("Obesidade Grau I"),
    OBESITY_GRADE_II("Obesidade Grau II (Severa)"),
    OBESITY_GRADE_III("Obesidade III (Mórbida)");

    private final String description;

    IMCClassification(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
