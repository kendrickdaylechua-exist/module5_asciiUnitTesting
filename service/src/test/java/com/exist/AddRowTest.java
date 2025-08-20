package com.exist;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AddRowTest {
    private final PrintStream standardOut = System.out;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();

    private List<List<Element>> sampleTableData;
    private List<Element> innerList3;

    private final TableServiceImpl spyService = spy(new TableServiceImpl());

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
        innerList1.add(new Element("aaa", "bbb"));
        innerList1.add(new Element("bbb", "ccc"));
        innerList2.add(new Element("qwer", "asdf"));
        innerList2.add(new Element("tttt", "tttt"));
        innerList2.add(new Element("HHH", "hhh"));
        sampleTableData.add(innerList1);
        sampleTableData.add(innerList2);

        innerList3 = List.of(
                new Element("newKey1", "newValue1"),
                new Element("newKey2", "newValue2")
        );
    }

    @AfterEach
    void cleanUp() {
        System.setOut(standardOut);
        sampleTableData.clear();
    }

    @Test
    void testValidAddFirst() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getIntInput("No. of Cells: "))
                    .thenReturn(2);
            mockInputUtil.when(() -> InputUtil.getIntInput("Row Index (Table Size: " + sampleTableData.size() + "): "))
                    .thenReturn(0);
            doReturn(innerList3).when(spyService).asciiGenerator(2);
            spyService.addRow(sampleTableData);

            assertEquals(innerList3, sampleTableData.get(0));
            assertEquals(3, sampleTableData.size());
            assertEquals("newKey1", sampleTableData.get(0).get(0).getKey());
            assertEquals("newKey2", sampleTableData.get(0).get(1).getKey());
            assertEquals("newValue1", sampleTableData.get(0).get(0).getValue());
            assertEquals("newValue2", sampleTableData.get(0).get(1).getValue());
        }
    }

    @Test
    void testValidAddLast() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getIntInput("No. of Cells: "))
                    .thenReturn(2);
            mockInputUtil.when(() -> InputUtil.getIntInput("Row Index (Table Size: " + sampleTableData.size() + "): "))
                    .thenReturn(2);
            doReturn(innerList3).when(spyService).asciiGenerator(2);
            spyService.addRow(sampleTableData);

            assertEquals(innerList3, sampleTableData.get(2));
            assertEquals(3, sampleTableData.size());
            assertEquals("newKey1", sampleTableData.get(2).get(0).getKey());
            assertEquals("newKey2", sampleTableData.get(2).get(1).getKey());
            assertEquals("newValue1", sampleTableData.get(2).get(0).getValue());
            assertEquals("newValue2", sampleTableData.get(2).get(1).getValue());
        }
    }

    @Test
    void testValidAddMiddle() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getIntInput("No. of Cells: "))
                    .thenReturn(2);
            mockInputUtil.when(() -> InputUtil.getIntInput("Row Index (Table Size: " + sampleTableData.size() + "): "))
                    .thenReturn(1);
            doReturn(innerList3).when(spyService).asciiGenerator(2);
            spyService.addRow(sampleTableData);

            assertEquals(innerList3, sampleTableData.get(1));
            assertEquals(3, sampleTableData.size());
            assertEquals("newKey1", sampleTableData.get(1).get(0).getKey());
            assertEquals("newKey2", sampleTableData.get(1).get(1).getKey());
            assertEquals("newValue1", sampleTableData.get(1).get(0).getValue());
            assertEquals("newValue2", sampleTableData.get(1).get(1).getValue());
        }
    }

    @Test
    void testZeroNumOfCellsThenValid() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getIntInput("No. of Cells: "))
                    .thenReturn(0).thenReturn(2);
            mockInputUtil.when(() -> InputUtil.getIntInput("Row Index (Table Size: " + sampleTableData.size() + "): "))
                    .thenReturn(1);
            doReturn(innerList3).when(spyService).asciiGenerator(2);
            spyService.addRow(sampleTableData);

            assertEquals("The number of cells cannot be 0 or less. Please try again...", outputStreamCaptor.toString().trim());
            assertEquals(innerList3, sampleTableData.get(1));
        }
    }

    @Test
    void testNegativeValueNumOfCellsThenValid() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getIntInput("No. of Cells: "))
                    .thenReturn(-1).thenReturn(2);
            mockInputUtil.when(() -> InputUtil.getIntInput("Row Index (Table Size: " + sampleTableData.size() + "): "))
                    .thenReturn(1);
            doReturn(innerList3).when(spyService).asciiGenerator(2);
            spyService.addRow(sampleTableData);

            assertEquals("The number of cells cannot be 0 or less. Please try again...", outputStreamCaptor.toString().trim());
            assertEquals(innerList3, sampleTableData.get(1));
        }
    }

    @Test
    void testIndexOutOfBoundsThenValid() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getIntInput("No. of Cells: "))
                    .thenReturn(2);
            mockInputUtil.when(() -> InputUtil.getIntInput("Row Index (Table Size: " + sampleTableData.size() + "): "))
                    .thenReturn(99).thenReturn(1);
            doReturn(innerList3).when(spyService).asciiGenerator(2);
            spyService.addRow(sampleTableData);
            assertEquals("Index Out of Bound! Please try again...", outputStreamCaptor.toString().trim());
            assertEquals(innerList3, sampleTableData.get(1));
        }
    }
}
