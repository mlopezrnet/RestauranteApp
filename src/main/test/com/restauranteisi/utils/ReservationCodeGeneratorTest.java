package com.restauranteisi.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReservationCodeGeneratorTest {
    @Test
@DisplayName("Prueba de Generación de código no es nulo")
    void testGenerateCode() {
        int length = 8;
        ReservationCodeGenerator YourClass = new ReservationCodeGenerator() ;
        String generatedCode = YourClass.generateCode(length);
        assertNotNull(generatedCode);
        assertEquals(length, generatedCode.length());
        assertTrue(generatedCode.matches("[A-Za-z0-9]+"));
    }
    @Test
    @DisplayName("Prueba Generación de código lanza excepción")
    void testGenerateCodeWithInvalidLength() {
        ReservationCodeGenerator YourClass = new ReservationCodeGenerator();
        assertThrows(IllegalArgumentException.class, () -> YourClass.generateCode(0));
        assertThrows(IllegalArgumentException.class, () -> YourClass.generateCode(-5));
    }

    @Test
    @DisplayName("Prueba Generación de código con longitud predeterminada")
    void testGenerateDefaultCode() {
        String generatedCode = ReservationCodeGenerator.generateCode();
        assertNotNull(generatedCode);
        assertEquals(5, generatedCode.length());
        assertTrue(generatedCode.matches("[A-Z0-9]+"));
    }
}