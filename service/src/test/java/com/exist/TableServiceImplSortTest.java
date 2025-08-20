package com.exist;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TableServiceImplSortTest {
    private final PrintStream standardOut = System.out;
    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();

    private final TableServiceImpl realService = new TableServiceImpl();

    private List<List<Element>> sampleTableData;

    @BeforeEach
    void setup() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errorStreamCaptor));
    }

    @BeforeEach
    void createTableAndVariables() {
        sampleTableData = new LinkedList<>();
        List<Element> innerList1 = new ArrayList<>();
        List<Element> innerList2 = new ArrayList<>();
        innerList1.add(new Element("ggg", "hhh"));
        innerList1.add(new Element("aaa", "bbb"));
        innerList1.add(new Element("aaa", "ccc"));
        innerList2.add(new Element("qwer", "asdf"));
        innerList2.add(new Element("tttt", "tttt"));
        innerList2.add(new Element("HHH", "hhh"));
        sampleTableData.add(innerList1);
        sampleTableData.add(innerList2);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
        System.setErr(standardErr);
    }

    @Test
    void testInvalidInput() {
        String sampleInput = "not_valid";
        RuntimeException runtimeException = assertThrows(
                InputMismatchException.class, () -> realService.sortCheckInput(sampleInput, sampleTableData)
        );
        assertEquals(null, runtimeException.getMessage());
    }

    @Test
    void testInvalidInputNotNumber() {
        String sampleInput = "not_valid - not_valid";
        RuntimeException runtimeException = assertThrows(
                NumberFormatException.class, () -> realService.sortCheckInput(sampleInput, sampleTableData)
        );
        assertEquals("For input string: \"not_valid \"", runtimeException.getMessage());
    }

    @Test
    void testInvalidInputSortingOption() {
        String sampleInput = "not_valid";
        realService.checkSortingOption(1, sampleInput, sampleTableData);
        assertEquals("Invalid Input!", outputStreamCaptor.toString().trim());
    }

    @Test
    void testAscending() {
        try (MockedStatic<InputUtil> mockedInput = Mockito.mockStatic(InputUtil.class)) {
            mockedInput.when(() -> InputUtil.getStringInput("Row to sort: "))
                    .thenReturn("0 - asc");
            sampleTableData = realService.sort(sampleTableData);
            assertEquals("aaa", sampleTableData.get(0).get(0).getKey());
            assertEquals("bbb", sampleTableData.get(0).get(0).getValue());
            assertEquals("aaa", sampleTableData.get(0).get(1).getKey());
            assertEquals("ccc", sampleTableData.get(0).get(1).getValue());
            assertEquals("ggg", sampleTableData.get(0).get(2).getKey());
            assertEquals("hhh", sampleTableData.get(0).get(2).getValue());
        }
    }

    @Test
    void testDescending() {
        try (MockedStatic<InputUtil> mockedInput = Mockito.mockStatic(InputUtil.class)) {
            mockedInput.when(() -> InputUtil.getStringInput("Row to sort: "))
                    .thenReturn("0 - desc");
            sampleTableData = realService.sort(sampleTableData);
            assertEquals("ggg", sampleTableData.get(0).get(0).getKey());
            assertEquals("hhh", sampleTableData.get(0).get(0).getValue());
            assertEquals("aaa", sampleTableData.get(0).get(1).getKey());
            assertEquals("ccc", sampleTableData.get(0).get(1).getValue());
            assertEquals("aaa", sampleTableData.get(0).get(2).getKey());
            assertEquals("bbb", sampleTableData.get(0).get(2).getValue());
        }
    }

    @Test
    void testInvalidInputEmptyNumber() {
        String sampleInput = "- not_valid";
        RuntimeException runtimeException = assertThrows(
                NumberFormatException.class, () -> realService.sortCheckInput(sampleInput, sampleTableData)
        );
        assertEquals("For input string: \"\"", runtimeException.getMessage());
    }

    @Test
    void testInvalidInputEmptySortingOption() {
        String sampleInput = "";
        realService.checkSortingOption(1, sampleInput, sampleTableData);
        assertEquals("Invalid Input!", outputStreamCaptor.toString().trim());
    }

    @Test
    void testIfTableIsEmpty() {
        sampleTableData.clear();
        realService.sort(sampleTableData);
        assertEquals("Table is empty. You cannot edit anything!", errorStreamCaptor.toString().trim());
    }
}
