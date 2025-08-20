package com.exist;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InputUtilTest {
    private final PrintStream standardOut = System.out;
    private final InputStream standardIn = System.in;
    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setup() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errorStreamCaptor));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
        System.setIn(standardIn);
        System.setErr(standardErr);
        InputUtil.closeScanner();
    }

    @Test
    void testStringInputValidString() {
        String sampleInput = "test\n";
        System.setIn(new ByteArrayInputStream(sampleInput.getBytes()));
        resetScanner();
        String result = InputUtil.getStringInput("Test Prompt");
        assertTrue(outputStreamCaptor.toString().contains("Test Prompt"));
        assertEquals("test", result);
    }

    @Test
    void testStringInputEmptyString() {
        String simulatedInput = "\nhello\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        resetScanner();

        String result = InputUtil.getStringInput("Enter something: ");
        String errOutput = errorStreamCaptor.toString().replace("\r", "");
        assertTrue(outputStreamCaptor.toString().contains("Enter something: "));
        assertTrue(errOutput.contains("Please input something..."));
        assertEquals("hello", result);
    }


    @Test
    void testIntInputValidInt() {
        String sampleInput = "123\n";
        System.setIn(new ByteArrayInputStream(sampleInput.getBytes()));
        resetScanner();
        int result = InputUtil.getIntInput("Test Prompt");
        assertTrue(outputStreamCaptor.toString().contains("Test Prompt"));
        assertEquals(123, result);
    }

    @Test
    void testIntInputWithInvalidThenValid() {
        String sampleInput = "abc\n123\n";
        System.setIn(new ByteArrayInputStream(sampleInput.getBytes()));
        resetScanner();

        int result = InputUtil.getIntInput("Enter number: ");
        String errOutput = errorStreamCaptor.toString();
        assertTrue(outputStreamCaptor.toString().contains("Enter number: "));
        assertTrue(errOutput.contains("Your input is not a number. Please try again..."));
        assertEquals(123, result);
    }

    private void resetScanner() {
        try {
            java.lang.reflect.Field scannerField = InputUtil.class.getDeclaredField("scanner");
            scannerField.setAccessible(true);
            scannerField.set(null, new java.util.Scanner(System.in));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
