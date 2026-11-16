package com.samuel.cli;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import com.samuel.exception.InvalidDataException;
import com.samuel.model.Person;
import com.samuel.service.CalculateIMC;

public class IMCCli {

    private Scanner scanner;
    private PrintStream out;
    private CalculateIMC calculateImc;
    
    public IMCCli() {
        this(System.in, System.out, new CalculateIMC());
    }

    public IMCCli(InputStream in, PrintStream out, CalculateIMC calculateIMC) {
        this.scanner = new Scanner(in);
        this.out = out;
        this.calculateImc = calculateIMC;
    }
    
    public void init() {
        int option = -1;
        do {
            showMenu();
            
            try {
                out.print("Digite a operação: ");
                option = Integer.parseInt(scanner.nextLine());
                readOption(option);
            } catch (RuntimeException e) {
                out.println("[Erro Inesperado]: " + e.getMessage());
            }
    
            if (option != 0) {
                pause();
            } 
        } while (option != 0);
    }

    private void showMenu() {
        out.printf("""
            \n
            %s
            ===   Calculadora de IMC   ===
            %s
            0 - sair
            1 - calcular IMC
            %s\n
            """, printLine(), printLine(), printLine());
    }

    private void readOption(int option) {
        switch (option) {
            case 0 -> {
                out.println("Encerrando calculadora de IMC...");
            }
            case 1 -> {
                try {
                    Person person = readPerson();
                    String result = calculateImc.getResult(person);
                    out.println("\n" + result);
                } catch (InvalidDataException e) {
                    out.println("Erro de validação: " + e.getMessage());
                }
            }
            default -> {
                out.println("Opção inválida!");
            }
        }
    }

    private String readName() {
        String name = ""; 
        do {
            out.print("Digite seu nome: ");
            name = scanner.nextLine();
    
            if (name.isBlank()) {
                out.println("\nNome inválido!\nO nome não pode ser nulo ou vazio!");
            }
        } while (name.isBlank());
        return name;
    }

    private double readWeight() {
        double weight = 0.0;
        do {
            out.print("Digite seu peso: ");
            weight = Double.parseDouble(scanner.nextLine());

            if (weight <= 0 || !Double.isFinite(weight)) {
                out.println("\nPeso inválido!\nO peso não pode ser menor ou igual a zero e precisa ser um número finito!");
            }
        } while (weight <= 0 || !Double.isFinite(weight));
        return weight;
    }

    private double readHeight() {
        double height = 0.0;
        do {
            out.print("Digite sua altura: ");
            height = Double.parseDouble(scanner.nextLine());

            if (height <= 0 || !Double.isFinite(height)) {
                out.println("\nAltura inválida!\nA altura não pode ser menor ou igual a zero e precisa ser um número finito!");
            }
        } while (height <= 0 || !Double.isFinite(height));
        return height;
    }

    private Person readPerson() {
        return new Person(readName(), readWeight(), readHeight());
    }

    private String printLine() {
        return "=".repeat(30);
    }

    private void pause() {
        out.println("\nPrecione Enter para continuar...");
        scanner.nextLine();
    }
}
