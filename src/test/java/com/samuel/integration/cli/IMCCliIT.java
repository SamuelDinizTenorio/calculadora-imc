package com.samuel.integration.cli;

import com.samuel.cli.IMCCli;
import com.samuel.enums.IMCClassification;
import com.samuel.service.CalculateIMC;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IMC CLI Integration Tests")
class IMCCliIT {

    private ByteArrayOutputStream output;

    private IMCCli createCli(String input) {
        var in = new ByteArrayInputStream(
            input.getBytes(StandardCharsets.UTF_8)
        );

        output = new ByteArrayOutputStream();
        var out = new PrintStream(output, true, StandardCharsets.UTF_8);

        return new IMCCli(in, out, new CalculateIMC());
    }

    private String getOutput() {
        return output.toString(StandardCharsets.UTF_8);
    }

    @Nested
    @DisplayName("Menu Navigation Integration Tests")
    class MenuTests {

        @Test
        @DisplayName("Should exit the application when option 0 is selected")
        void shouldExitApplication() {
            var cli = createCli("0\n");
            cli.init();

            assertThat(getOutput())
                .contains("Encerrando calculadora de IMC...");
        }

        @Test
        @DisplayName("Should handle invalid menu option and exit cleanly afterwards")
        void shouldHandleInvalidOptionAndExit() {
            var cli = createCli("99\n\n0\n");
            cli.init();

            assertThat(getOutput())
                .contains(
                    "Opção inválida!",
                    "Encerrando calculadora de IMC..."
                );
        }

        @Test
        @DisplayName("Should handle non-numeric menu input and exit cleanly afterwards")
        void shouldHandleNonNumericMenuInputAndExit() {
            var cli = createCli("abc\n\n0\n");
            cli.init();

            assertThat(getOutput())
                .contains(
                    "Opção inválida! Digite um número.",
                    "Encerrando calculadora de IMC..."
                );
        }
    }

    @Nested
    @DisplayName("IMC Calculation Flow Integration Tests")
    class CalculationTests {

        private static Stream<Arguments> calculationScenarios() {
            return Stream.of(
                Arguments.of("Lucas", 50.0, 1.80, 15.43, IMCClassification.UNDERWEIGHT.getDescription()),
                Arguments.of("Maria", 65.0, 1.70, 22.49, IMCClassification.NORMAL_WEIGHT.getDescription()),
                Arguments.of("João", 80.0, 1.70, 27.68, IMCClassification.OVERWEIGHT.getDescription()),
                Arguments.of("Carlos", 95.0, 1.70, 32.87, IMCClassification.OBESITY_GRADE_I.getDescription()),
                Arguments.of("Ana", 110.0, 1.70, 38.06, IMCClassification.OBESITY_GRADE_II.getDescription()),
                Arguments.of("Pedro", 130.0, 1.70, 44.98, IMCClassification.OBESITY_GRADE_III.getDescription())
            );
        }

        @ParameterizedTest(name = "Should calculate IMC for {0} and classify as {4}")
        @MethodSource("calculationScenarios")
        @DisplayName("Should integrate CLI with CalculateIMC across all IMC classifications")
        void shouldCalculateAndClassifyIMCForAllCategories(String name, double weight, double height, double expectedImc, String expectedClassification) {
            String input = String.format("1\n%s\n%s\n%s\n\n0\n", name, weight, height);
            var cli = createCli(input);
            cli.init();

            assertThat(getOutput())
                .contains("Resultado do IMC de " + name + ":")
                .contains(String.format("IMC: %.2f", expectedImc))
                .contains("Classificação: " + expectedClassification)
                .contains("Encerrando calculadora de IMC...");
        }

        @Test
        @DisplayName("Should execute multiple calculations in sequence within the same session")
        void shouldExecuteMultipleCalculationsInSingleSession() {
            String input = """
                1
                João
                80
                1.70
                
                1
                Maria
                65
                1.70
                
                0
                """;

            var cli = createCli(input);
            cli.init();

            assertThat(getOutput())
                .contains("Resultado do IMC de João:")
                .contains("Classificação: Sobrepeso")
                .contains("Resultado do IMC de Maria:")
                .contains("Classificação: Peso normal")
                .contains("Encerrando calculadora de IMC...");
        }
    }

    @Nested
    @DisplayName("Input Validation and Recovery Integration Tests")
    class InputValidationAndRecoveryTests {

        @Test
        @DisplayName("Should retry when user enters invalid inputs during calculation and proceed successfully")
        void shouldRecoverFromInvalidInputsDuringCalculation() {
            String input = """
                1
                
                Maria
                0
                80
                -1
                1.70
                
                0
                """;

            var cli = createCli(input);
            cli.init();

            assertThat(getOutput())
                .contains(
                    "Nome inválido! O nome não pode está em branco.",
                    "Peso inválido! Digite um número válido.",
                    "Altura inválida! Digite um número válido.",
                    "Resultado do IMC de Maria:",
                    "Classificação: Sobrepeso",
                    "Encerrando calculadora de IMC..."
                );
        }

        @Test
        @DisplayName("Should recover from invalid menu options, calculate IMC and exit successfully")
        void shouldRecoverFromInvalidMenuOptionAndCompleteCalculation() {
            String input = """
                invalido
                
                99
                
                1
                Carlos
                70
                1.75
                
                0
                """;

            var cli = createCli(input);
            cli.init();

            assertThat(getOutput())
                .contains(
                    "Opção inválida! Digite um número.",
                    "Opção inválida!",
                    "Resultado do IMC de Carlos:",
                    "Classificação: Peso normal",
                    "Encerrando calculadora de IMC..."
                );
        }
    }
}
