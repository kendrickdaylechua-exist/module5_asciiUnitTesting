package com.exist;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InvalidFormatExceptionTest {
    @Test
    void testThrowingInvalidFormatException() {
        InvalidFormatException exception = assertThrows(
                InvalidFormatException.class,
                () -> {
                    throw new InvalidFormatException("Invalid format detected!");
                }
        );
        assertEquals("Invalid format detected!", exception.getMessage());
    }
}
