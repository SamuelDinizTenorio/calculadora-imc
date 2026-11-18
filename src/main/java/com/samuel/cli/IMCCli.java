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
            } catch (NumberFormatException e) {
                out.println("Opção inválida! Digite um número.");
            }catch (RuntimeException e) {
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
    
            if (name == null || name.isBlank()) {
                out.println("Nome inválido! O nome não pode está em branco.");
            }
        } while (name == null || name.isBlank());
        return name;
    }

    private double readWeight() {
        double weight = 0.0;
        String errorMessage = "Peso inválido! Digite um número válido.";
        do {
            try {
                out.print("Digite seu peso: ");
                weight = Double.parseDouble(scanner.nextLine());
    
                if (weight <= 0 || !Double.isFinite(weight)) {
                    out.println(errorMessage);
                }
            } catch (NumberFormatException e) {
                out.println(errorMessage);
            }
        } while (weight <= 0 || !Double.isFinite(weight));
        return weight;
    }

    private double readHeight() {
        double height = 0.0;
        String errorMessage = "Altura inválida! Digite um número válido.";
        do {
            try {
                out.print("Digite sua altura: ");
                height = Double.parseDouble(scanner.nextLine());
    
                if (height <= 0 || !Double.isFinite(height)) {
                    out.println(errorMessage);
                }
            } catch (NumberFormatException e) {
                out.println(errorMessage);
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
