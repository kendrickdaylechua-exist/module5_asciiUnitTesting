package com.exist;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

public class TableServiceImplSearchTest {
    private final PrintStream standardOut = System.out;
    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();
    private List<List<Element>> sampleTableData;

    private final TableServiceImpl realService = new TableServiceImpl();

    @BeforeEach
    void setup() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errorStreamCaptor));
    }

    @BeforeEach
    void createSampleTable() {
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
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
        System.setErr(standardErr);
    }

    @Test
    void testSearchNotFound() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("Search: "))
                    .thenReturn("qqq");
            realService.search(sampleTableData);
            String expectedOutput = "No result found";
            assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
        }
    }

    @Test
    void testSearchOneCharacter() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("Search: "))
                    .thenReturn("a");
            realService.search(sampleTableData);
            String expectedOutput =
                    "3 <a> at key of [0, 0]\r\n" +
                    "1 <a> at value of [1, 0]";

            assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
        }
    }

    @Test
    void testSearchTwoCharacters() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("Search: "))
                    .thenReturn("aa");
            realService.search(sampleTableData);
            String expectedOutput = "2 <aa> at key of [0, 0]";
            assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
        }
    }

    @Test
    void testSearchKeyAndValue() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("Search: "))
                    .thenReturn("t");
            realService.search(sampleTableData);
            String expectedOutput = "4 <t> at key and 4 <t> at value of [1, 1]";
            assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
        }
    }

    @Test
    void testSearchCaseSensitivity() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("Search: "))
                    .thenReturn("HHH");
            realService.search(sampleTableData);
            String expectedOutput = "1 <HHH> at key of [1, 2]";
            assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
        }
    }

    @Test
    void testSearchEmptyTable() {
        List<List<Element>> sampleEmptyTable = new ArrayList<>();
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("Search: "))
                    .thenReturn("HHH");
            realService.search(sampleEmptyTable);
            String expectedOutput = "No result found";
            assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
        }
    }

    @Test
    void testSearchNull() {
        List<List<Element>> sampleEmptyTable = new ArrayList<>();
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("Search: "))
                    .thenReturn(null);
            realService.search(sampleEmptyTable);
            String expectedOutput = "No result found";
            assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
        }
    }
}
