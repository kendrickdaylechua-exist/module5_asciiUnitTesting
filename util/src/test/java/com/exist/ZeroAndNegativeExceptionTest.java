package com.exist;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ZeroAndNegativeExceptionTest {
    @Test
    void testThrowingZeroAndNegativeException() {
        ZeroAndNegativeException exception = assertThrows(
                ZeroAndNegativeException.class,
                () -> {
                    throw new ZeroAndNegativeException("Value must be greater than zero!");
                }
        );
        assertEquals("Value must be greater than zero!", exception.getMessage());
    }
}
