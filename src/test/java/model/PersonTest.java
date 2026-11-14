package model;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.samuel.exception.InvalidDataException;
import com.samuel.model.Person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PersonTest {

    @Test
    @DisplayName("Should create a person with valid data")
    void shouldCreatePersonWithValidData() {
        String expectedName = "Maria";
        double expectedWeight = 80;
        double expectedHeight = 1.70;

        var person = new Person(expectedName, expectedWeight, expectedHeight);

        assertThat(person.getName()).isEqualTo(expectedName);
        assertThat(person.getWeight()).isEqualTo(expectedWeight);
        assertThat(person.getHeight()).isEqualTo(expectedHeight);
    }

    @ParameterizedTest 
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("Should throw exception when name is invalid")
    void shouldThrowExceptionWhenNameIsInvalid(String name) {
        assertThatThrownBy(() -> new Person(name, 80.0, 1.80))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("O nome");
    }

    private static Stream<Double> invalidMeasurements() {
        return Stream.of(
            0.0,
            -1.0,
            -80.0,
            Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY
        );
    }

    @ParameterizedTest
    @MethodSource("invalidMeasurements")
    @DisplayName("Should throw exception when weight is invalid") 
    void shouldThrowExceptionWhenWeightIsInvalid(double weight) {
        assertThatThrownBy(() -> new Person("Maria", weight, 1.80))
                .isInstanceOf(InvalidDataException.class)
                .hasMessage("Peso inválido!");
    }

    @ParameterizedTest
    @MethodSource("invalidMeasurements")
    @DisplayName("Should throw exception when height is invalid") 
    void shouldThrowExceptionWhenHeightIsInvalid(double height) {
        assertThatThrownBy(() -> new Person("Maria", 80.0, height))
                .isInstanceOf(InvalidDataException.class)
                .hasMessage("Altura inválida!");
    }
}
