package com.samuel.unit.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.samuel.cli.IMCCli;
import com.samuel.enums.IMCClassification;
import com.samuel.exception.InvalidDataException;
import com.samuel.model.Person;
import com.samuel.service.CalculateIMC;

@ExtendWith(MockitoExtension.class)
@DisplayName("IMC CLI Unit Tests")
class IMCCliTest {

    @Mock
    private CalculateIMC calculateIMC;

    private ByteArrayOutputStream outputStream;
    private PrintStream printStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream, true, StandardCharsets.UTF_8);
    }

    private IMCCli createCliWithInput(String input) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        return new IMCCli(inputStream, printStream, calculateIMC);
    }

    private String getOutput() {
        return outputStream.toString(StandardCharsets.UTF_8);
    }

    private Person capturePerson() {
        ArgumentCaptor<Person> captor =
            ArgumentCaptor.forClass(Person.class);

        verify(calculateIMC).getResult(captor.capture());

        return captor.getValue();
    }

    private String expectedResult(String name) {
        String expectedClassification = 
                IMCClassification.NORMAL_WEIGHT.getDescription();

        return String.format("""
            Resultado do IMC de %s:
            IMC: 22,86
            Classificação: %s
            """, name, expectedClassification);
    }

    @Test
    @DisplayName("Should instantiate IMCCli with default constructor")
    void shouldInstantiateWithDefaultConstructor() {
        IMCCli cli = new IMCCli();
        assertThat(cli).isNotNull();
    }

    @Nested
    @DisplayName("Menu and Navigation Tests")
    class MenuNavigationTests {

        @Test
        @DisplayName("Should display menu headers and options")
        void shouldDisplayMenuHeadersAndOptions() {
            // Arrange
            IMCCli cli = createCliWithInput("0\n");

            // Act
            cli.init();

            // Assert
            assertThat(getOutput())
                .contains(
                    "===   Calculadora de IMC   ===",
                    "0 - sair",
                    "1 - calcular IMC",
                    "Digite a operação: "
                );
        }

        @Test
        @DisplayName("Should display pause prompt when an operation is executed")
        void shouldDisplayPausePromptAfterOperation() {
            // Arrange
            IMCCli cli = createCliWithInput("99\n\n0\n");

            // Act
            cli.init();

            // Assert
            assertThat(getOutput())
                .contains("Pressione Enter para continuar...");
        }

        @Test
        @DisplayName("Should exit successfully when option 0 is selected")
        void shouldExitWhenOptionZeroIsSelected() {
            // Arrange
            String simulatedInput = "0\n";
            IMCCli cli = createCliWithInput(simulatedInput);

            // Act
            cli.init();

            // Assert
            assertThat(getOutput())
                .contains("Encerrando calculadora de IMC...");

            verifyNoInteractions(calculateIMC);
        }

        @ParameterizedTest 
        @ValueSource(strings = {"abc", "#", "$d", "", " ", "   "})
        @DisplayName("Should handle invalid non-numeric option gracefully")
        void shouldHandleInvalidNonNumericOption(String nonNumericOption) {
            // Arrange
            String simulatedInput = nonNumericOption + "\n\n0\n";
            IMCCli cli = createCliWithInput(simulatedInput);

            // Act
            cli.init();

            // Assert
            assertThat(getOutput())
                .contains("Opção inválida! Digite um número correspondente ao menu.");

            verifyNoInteractions(calculateIMC);
        }

        @ParameterizedTest 
        @ValueSource(strings = {"99", "-1", "-99"})
        @DisplayName("Should handle out-of-range menu option")
        void shouldHandleOutOfRangeOption(String outRangeOption) {
            // Arrange
            String simulatedInput = outRangeOption + "\n\n0\n";
            IMCCli cli = createCliWithInput(simulatedInput);

            // Act
            cli.init();

            // Assert
            assertThat(getOutput())
                .contains("Opção não reconhecida! Escolha uma opção disponível no menu.");

            verifyNoInteractions(calculateIMC);
        }
    }

    @Nested
    @DisplayName("IMC Calculation Flow Tests")
    class ImcCalculationFlowTests {

        @Test
        @DisplayName("Should successfully calculate IMC when valid data is provided")
        void shouldCalculateImcSuccessfully() {
            // Arrange
            String name = "João";
            double weight = 70.0;
            double height = 1.75;

            String simulatedInput = String.format(
                "1\n%s\n%s\n%s\n\n0\n",
                name, weight, height
            );

            String expectedMessage = expectedResult(name);

            IMCCli cli = createCliWithInput(simulatedInput);
            
            when(calculateIMC.getResult(any(Person.class)))
                .thenReturn(expectedMessage);

            // Act
            cli.init();

            // Assert
            assertThat(getOutput())
                .contains(expectedMessage);

            Person capturedPerson = capturePerson();

            assertThat(capturedPerson.getName()).isEqualTo(name);
            assertThat(capturedPerson.getWeight()).isEqualTo(weight);
            assertThat(capturedPerson.getHeight()).isEqualTo(height);
        }

        @Test
        @DisplayName("Should allow multiple calculations in a single session")
        void shouldAllowMultipleCalculationsInSingleSession() {
            // Arrange
            String simulatedInput = "1\nJoão\n70.0\n1.75\n\n1\nMaria\n60.0\n1.65\n\n0\n";
            IMCCli cli = createCliWithInput(simulatedInput);

            when(calculateIMC.getResult(any(Person.class)))
                .thenReturn("Resultado do IMC de João:\nIMC: 22,86\nClassificação: Peso normal")
                .thenReturn("Resultado do IMC de Maria:\nIMC: 22,04\nClassificação: Peso normal");

            // Act
            cli.init();

            // Assert
            assertThat(getOutput())
                .contains("Resultado do IMC de João:")
                .contains("Resultado do IMC de Maria:")
                .contains("Encerrando calculadora de IMC...");

            ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
            verify(calculateIMC, org.mockito.Mockito.times(2)).getResult(captor.capture());

            var persons = captor.getAllValues();
            assertThat(persons).hasSize(2);
            assertThat(persons.get(0).getName()).isEqualTo("João");
            assertThat(persons.get(0).getWeight()).isEqualTo(70.0);
            assertThat(persons.get(0).getHeight()).isEqualTo(1.75);
            assertThat(persons.get(1).getName()).isEqualTo("Maria");
            assertThat(persons.get(1).getWeight()).isEqualTo(60.0);
            assertThat(persons.get(1).getHeight()).isEqualTo(1.65);
        }
    }

    @Nested
    @DisplayName("Input Validation Tests")
    class InputValidationTests {

        @ParameterizedTest(name = "Should reject blank name: [{0}]")
        @ValueSource(strings = {"", " ", "   "})
        @DisplayName("Should retry name input when the name is blank")
        void shouldRetryWhenNameIsBlank(String invalidName) {
            // Arrange
            String validName = "João";
            double weight = 70.0;
            double height = 1.75;

            String simulatedInput = String.format(
                "1\n%s\n%s\n%s\n%s\n\n0\n", 
                invalidName, validName, weight, height
            );

            String expectedMessage = expectedResult(validName);

            String expectedErrorMessage = 
                "O nome não pode ficar em branco. Digite novamente.";

            IMCCli cli = createCliWithInput(simulatedInput);

            when(calculateIMC.getResult(any(Person.class)))
                .thenReturn(expectedMessage);

            // Act
            cli.init();

            // Assert
            assertThat(getOutput())
                .contains(expectedErrorMessage, expectedMessage);

            Person capturedPerson = capturePerson();

            assertThat(capturedPerson.getName()).isEqualTo(validName);
            assertThat(capturedPerson.getWeight()).isEqualTo(weight);
            assertThat(capturedPerson.getHeight()).isEqualTo(height);
        }

        @ParameterizedTest(name = "Should reject weight: [{0}]")
        @ValueSource(doubles = {0.0, -1.0, -80.0, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY})
        @DisplayName("Should retry when weight is negative, zero, infinite or not a number")
        void shouldRetryWhenWeightIsInvalid(double invalidWeight) {
            // Arrange
            String name = "João";
            double validWeight = 70.0;
            double height = 1.75;

            String simulatedInput = String.format(
                "1\n%s\n%s\n%s\n%s\n\n0\n", 
                name, invalidWeight, validWeight, height
            );

            String expectedMessage = expectedResult(name);

            String expectedErrorMessage = 
                "Peso inválido! Informe um número finito maior que zero (em kg).";

            IMCCli cli = createCliWithInput(simulatedInput);

            when(calculateIMC.getResult(any(Person.class)))
                .thenReturn(expectedMessage);

            // Act
            cli.init();

            // Assert
            assertThat(getOutput())
                .contains(expectedErrorMessage, expectedMessage);

            Person capturedPerson = capturePerson();

            assertThat(capturedPerson.getName()).isEqualTo(name);
            assertThat(capturedPerson.getWeight()).isEqualTo(validWeight);
            assertThat(capturedPerson.getHeight()).isEqualTo(height);
        }

        @ParameterizedTest(name = "Should reject height: [{0}]")
        @ValueSource(doubles = {0.0, -1.0, -80.0, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY})
        @DisplayName("Should retry when height is negative, zero, infinite or not a number")
        void shouldRetryWhenHeightIsInvalid(double invalidHeight) {
            // Arrange
            String name = "João";
            double weight = 70.0;
            double validHeight = 1.75;

            String simulatedInput = String.format(
                "1\n%s\n%s\n%s\n%s\n\n0\n", 
                name, weight, invalidHeight, validHeight
            );

            String expectedMessage = expectedResult(name);

            String expectedErrorMessage = 
                "Altura inválida! Informe um número finito maior que zero (em metros).";

            IMCCli cli = createCliWithInput(simulatedInput);

            when(calculateIMC.getResult(any(Person.class)))
                .thenReturn(expectedMessage);

            // Act
            cli.init();

            // Assert
            assertThat(getOutput())
                .contains(expectedErrorMessage, expectedMessage);

            Person capturedPerson = capturePerson();

            assertThat(capturedPerson.getName()).isEqualTo(name);
            assertThat(capturedPerson.getWeight()).isEqualTo(weight);
            assertThat(capturedPerson.getHeight()).isEqualTo(validHeight);
        }

        @ParameterizedTest(name = "Should reject non-numeric weight: [{0}]")
        @ValueSource(strings = {"abc", "#", "$d", "", " ", "   "})
        @DisplayName("Should retry when weight input is not a valid number")
        void shouldRetryWhenWeightIsInvalidNumericInputs(String invalidWeight) {
            // Arrange
            String name = "João";
            double validWeight = 70.0;
            double height = 1.75;

            String simulatedInput = String.format(
                "1\n%s\n%s\n%s\n%s\n\n0\n", 
                name, invalidWeight, validWeight, height
            );

            String expectedMessage = expectedResult(name);

            String expectedErrorMessage = 
                "Peso inválido! Informe um número finito maior que zero (em kg).";

            IMCCli cli = createCliWithInput(simulatedInput);

            when(calculateIMC.getResult(any(Person.class)))
                .thenReturn(expectedMessage);

            // Act
            cli.init();

            // Assert
            assertThat(getOutput())
                .contains(expectedErrorMessage, expectedMessage);

            Person capturedPerson = capturePerson();

            assertThat(capturedPerson.getName()).isEqualTo(name);
            assertThat(capturedPerson.getWeight()).isEqualTo(validWeight);
            assertThat(capturedPerson.getHeight()).isEqualTo(height);
        }

        @ParameterizedTest(name = "Should reject non-numeric height: [{0}]")
        @ValueSource(strings = {"abc", "#", "$d", "", " ", "   "})
        @DisplayName("Should retry when height input is not a valid number")
        void shouldRetryWhenHeightIsInvalidNumericInputs(String invalidHeight) {
            // Arrange
            String name = "João";
            double weight = 70.0;
            double validHeight = 1.75;

            String simulatedInput = String.format(
                "1\n%s\n%s\n%s\n%s\n\n0\n", 
                name, weight, invalidHeight, validHeight
            );

            String expectedMessage = expectedResult(name);

            String expectedErrorMessage = 
                "Altura inválida! Informe um número finito maior que zero (em metros).";

            IMCCli cli = createCliWithInput(simulatedInput);

            when(calculateIMC.getResult(any(Person.class)))
                .thenReturn(expectedMessage);

            // Act
            cli.init();

            // Assert
            assertThat(getOutput())
                .contains(expectedErrorMessage, expectedMessage);

            Person capturedPerson = capturePerson();

            assertThat(capturedPerson.getName()).isEqualTo(name);
            assertThat(capturedPerson.getWeight()).isEqualTo(weight);
            assertThat(capturedPerson.getHeight()).isEqualTo(validHeight);
        }

        @Test
        @DisplayName("Should display validation error when calculation service throws InvalidDataException")
        void shouldDisplayValidationErrorWhenServiceThrowsInvalidDataException() {
            // Arrange
            String simulatedInput = "1\nJoão\n70.0\n1.75\n\n0\n";
            String errorMessage = "Não foi possível realizar o cálculo: dados inválidos.";

            IMCCli cli = createCliWithInput(simulatedInput);

            when(calculateIMC.getResult(any(Person.class)))
                .thenThrow(new InvalidDataException(errorMessage));

            // Act
            cli.init();

            // Assert
            assertThat(getOutput())
                .contains(errorMessage);

            verify(calculateIMC).getResult(any(Person.class));
        }

        @Test
        @DisplayName("Should handle unexpected exception from calculation service")
        void shouldHandleUnexpectedExceptionFromService() {
            // Arrange
            String simulatedInput = "1\nJoão\n70.0\n1.75\n\n0\n";
            String errorMessage = "Ocerreu um erro inesperado. Por favor tente novamente mais tarde.";

            IMCCli cli = createCliWithInput(simulatedInput);

            when(calculateIMC.getResult(any(Person.class)))
                .thenThrow(new RuntimeException(errorMessage));

            // Act
            cli.init();

            // Assert
            assertThat(getOutput())
                .contains(errorMessage);

            verify(calculateIMC).getResult(any(Person.class));
        }
    }
}
