package com.exist;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

public class TableServiceImplResetTest {
    private final PrintStream standardOut = System.out;
    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();

    private final TableServiceImpl realService = new TableServiceImpl();

    @BeforeEach
    void setup() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errorStreamCaptor));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
        System.setErr(standardErr);
    }

    @Test
    void testValid() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("Input table dimension: "))
                    .thenReturn("2x2");
            List<List<Element>> result = realService.reset();
            assertEquals(2, result.get(0).size());
            assertEquals(2, result.get(1).size());
            assertEquals(2, result.size());
        }
    }

    @Test
    void testNoDelimiterThenValid() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("Input table dimension: "))
                    .thenReturn("not_valid")
                    .thenReturn("1x2");
            List<Integer> result = realService.inputRowColumn();
            assertEquals("Input must be in the format of NxN", outputStreamCaptor.toString().trim());
            assertEquals(1, result.get(0));
            assertEquals(2, result.get(1));
        }
    }

    @Test
    void testZeroOrNegativeInputThenValid() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("Input table dimension: "))
                    .thenReturn("-1x0")
                    .thenReturn("1x2");
            List<Integer> result = realService.inputRowColumn();

            assertEquals("Input cannot be zero or negative", outputStreamCaptor.toString().trim());
            assertEquals(1, result.get(0));
            assertEquals(2, result.get(1));
        }
    }
}
