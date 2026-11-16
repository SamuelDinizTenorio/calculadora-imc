package service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.samuel.model.Person;
import com.samuel.service.CalculateIMC;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

public class CalculateIMCTest {

    private CalculateIMC calculateIMC;

    @BeforeEach
    void setUp() {
        calculateIMC = new CalculateIMC();
    }

    @Nested
    @DisplayName("Tests for IMC calculation and classification")
    class CalculationAndClassificationTests {

        @ParameterizedTest(
            name = "Weight: {0}kg, Height: {1}m, Expected IMC: {2}, Classification: {3}"
        )
        @CsvSource({
            "50.0, 1.80, 15.43, Abaixo do peso",
            "70.0, 1.80, 21.60, Peso normal",
            "85.0, 1.80, 26.23, Sobrepeso",
            "100.0, 1.80, 30.86, Obesidade Grau I",
            "120.0, 1.80, 37.04, Obesidade Grau II (Severa)",
            "140.0, 1.80, 43.21, Obesidade III (Mórbida)",
            "18.49, 1.0, 18.49, Abaixo do peso",
            "18.50, 1.0, 18.50, Peso normal",
            "24.99, 1.0, 24.99, Peso normal",
            "25.00, 1.0, 25.00, Sobrepeso",
            "29.99, 1.0, 29.99, Sobrepeso",
            "30.00, 1.0, 30.00, Obesidade Grau I",
            "34.99, 1.0, 34.99, Obesidade Grau I",
            "35.00, 1.0, 35.00, Obesidade Grau II (Severa)",
            "39.99, 1.0, 39.99, Obesidade Grau II (Severa)",
            "40.00, 1.0, 40.00, Obesidade III (Mórbida)"
        })
        @DisplayName("Should correctly calculate IMC, classify the result, and format the output string")
        void shouldCalculateAndClassifyImcCorrectly(
                double weight, 
                double height, 
                double expectedImc, 
                String expectedClassification
            ) {
            var person = new Person("Maria", weight, height);

            String result = calculateIMC.getResult(person);

            assertThat(result).isNotNull();
            assertThat(result).contains(person.getName());
            assertThat(result).contains(String.format("%.2f", expectedImc));
            assertThat(result).contains(expectedClassification);
        }
    }
}
