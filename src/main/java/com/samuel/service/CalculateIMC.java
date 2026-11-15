package com.samuel.service;

import com.samuel.enums.IMCClassification;
import com.samuel.model.Person;

public class CalculateIMC {

    public String getResult(Person person) {
        double imc = calculate(person.getWeight(), person.getHeight());
        IMCClassification classification = classify(imc);
        return String.format("""
            Resultado do IMC de %s:
            IMC: %.2f
            Classificação: %s
            """, person.getName(), imc, classification.getDescription());
    }

    private double calculate(double weight, double height) {
        return weight / (height * height);
    }

    private IMCClassification classify(double imc) {
        if (imc < 18.5) {
            return IMCClassification.UNDERWEIGHT;
        } else if (imc < 25) {
            return IMCClassification.NORMAL_WEIGHT;
        } else if (imc < 30) {
            return IMCClassification.OVERWEIGHT;
        } else if (imc < 35) {
            return IMCClassification.OBESITY_GRADE_I;
        } else if (imc < 40) {
            return IMCClassification.OBESITY_GRADE_II;
        }
        return IMCClassification.OBESITY_GRADE_III;
    }
}
