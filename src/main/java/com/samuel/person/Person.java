package com.samuel.person;

import com.samuel.exception.InvalidDataException;

public class Person {

    private String name;
    private double weight;
    private double height;

    public Person(String name, double weight, double height) {
        if (name == null || name.isBlank()) {
            throw new InvalidDataException("O nome não pode ser nulo ou vazio");
        }
        
        if (!Double.isFinite(weight) || weight <= 0) {
            throw new InvalidDataException("Peso inválido!");
        }

        if (!Double.isFinite(height) || height <= 0) {
            throw new InvalidDataException("Altura inválida!");
        }

        this.name = name;
        this.weight = weight;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }
}
