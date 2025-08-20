package com.exist;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

public class EditTest {
    private final PrintStream standardOut = System.out;
    private final PrintStream standardErr = System.err;
    private final InputStream standardIn = System.in;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();

    private List<List<Element>> sampleTableData;

    private final TableServiceImpl realService = new TableServiceImpl();

    private int editRow;
    private int editColumn;

    @BeforeEach
    void setup() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errorStreamCaptor));
    }

    @BeforeEach
    void createTableAndVariables() {
        sampleTableData = new ArrayList<>();
        List<Element> innerList1 = new ArrayList<>();
        List<Element> innerList2 = new ArrayList<>();
        innerList1.add(new Element("aaa", "bbb"));
        innerList1.add(new Element("bbb", "ccc"));
        innerList2.add(new Element("qwer", "asdf"));
        innerList2.add(new Element("tttt", "tttt"));
        innerList2.add(new Element("HHH", "hhh"));
        sampleTableData.add(innerList1);
        sampleTableData.add(innerList2);

        editRow = 0;
        editColumn = 0;
    }

    @AfterEach
    void cleanUp() {
        System.setOut(standardOut);
        System.setIn(standardIn);
        System.setErr(standardErr);
        sampleTableData.clear();
    }

    @Test
    void validInput() {
        String userInput = "[1,2]";
        List<Integer> result = realService.editCheckInput(userInput, sampleTableData);
        assertEquals(1, result.get(0));
        assertEquals(2, result.get(1));
    }

    @Test
    void invalidInputFormat() {
        String userInput = "not_valid";
        RuntimeException runtimeException = assertThrows(
                InvalidFormatException.class, () -> realService.editCheckInput(userInput, sampleTableData)
        );
        assertEquals("Invalid Input!", runtimeException.getMessage());
    }

    @Test
    void invalidInputRowNotInt() {
        String userInput = "[not_valid,0]";
        RuntimeException runtimeException = assertThrows(
                NumberFormatException.class, () -> realService.editCheckInput(userInput, sampleTableData)
        );
        assertEquals("For input string: \"not_valid\"", runtimeException.getMessage());
    }

    @Test
    void invalidInputColumnNotInt() {
        String userInput = "[0,not_valid]";
        RuntimeException runtimeException = assertThrows(
                NumberFormatException.class, () -> realService.editCheckInput(userInput, sampleTableData)
        );
        assertEquals("For input string: \"not_valid\"", runtimeException.getMessage());
    }

    @Test
    void invalidInputIndexOutOfBounds() {
        String userInput = "[2,0]";
        RuntimeException runtimeException = assertThrows(
                IndexOutOfBoundsException.class, () -> realService.editCheckInput(userInput, sampleTableData)
        );
        assertEquals("Element does not exists!", runtimeException.getMessage());
    }

    @Test
    void testEditKey() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("Key, Value, or Both?: "))
                    .thenReturn("key");
            mockInputUtil.when(() -> InputUtil.getStringInput("Input new key: "))
                    .thenReturn("new_key");
            realService.editCheckKeyValue(editRow, editColumn, sampleTableData);
            assertEquals("new_key", sampleTableData.get(0).get(0).getKey());
            assertEquals("bbb", sampleTableData.get(0).get(0).getValue());
        }
    }

    @Test
    void testEditValue() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("Key, Value, or Both?: "))
                    .thenReturn("value");
            mockInputUtil.when(() -> InputUtil.getStringInput("Input new value: "))
                    .thenReturn("new_value");
            realService.editCheckKeyValue(editRow, editColumn, sampleTableData);
            assertEquals("aaa", sampleTableData.get(0).get(0).getKey());
            assertEquals("new_value", sampleTableData.get(0).get(0).getValue());
        }
    }

    @Test
    void testEditBoth() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("Key, Value, or Both?: "))
                    .thenReturn("both");
            mockInputUtil.when(() -> InputUtil.getStringInput("Input both key and value: "))
                    .thenReturn("new_content");
            realService.editCheckKeyValue(editRow, editColumn, sampleTableData);
            assertEquals("new_content", sampleTableData.get(0).get(0).getKey());
            assertEquals("new_content", sampleTableData.get(0).get(0).getValue());
        }
    }

    @Test
    void testInvalidInputThenEditBoth() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("Key, Value, or Both?: "))
                    .thenReturn("not_valid").thenReturn("both");
            mockInputUtil.when(() -> InputUtil.getStringInput("Input both key and value: "))
                    .thenReturn("new_content");
            realService.editCheckKeyValue(editRow, editColumn, sampleTableData);
            String expectedOutput = "Invalid Input";
            assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
            assertEquals("new_content", sampleTableData.get(0).get(0).getKey());
            assertEquals("new_content", sampleTableData.get(0).get(0).getValue());
        }
    }

    @Test
    void testValid() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("Edit [row,column]: "))
                    .thenReturn("[0,0]");
            mockInputUtil.when(() -> InputUtil.getStringInput("Key, Value, or Both?: "))
                    .thenReturn("both");
            mockInputUtil.when(() -> InputUtil.getStringInput("Input both key and value: "))
                    .thenReturn("new_content");
            realService.edit(sampleTableData);
            assertEquals("new_content", sampleTableData.get(0).get(0).getKey());
            assertEquals("new_content", sampleTableData.get(0).get(0).getValue());
        }
    }

    @Test
    void testIfTableIsEmpty() {
        sampleTableData.clear();
        realService.edit(sampleTableData);
        assertEquals("Table is empty. You cannot edit anything!", errorStreamCaptor.toString().trim());
    }
}
