package com.exist;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class MainTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream standardOut = System.out;

    @Mock
    private TableServiceImpl mockTableService;

    @Mock
    private FileIOUtil mockFileIOUtil;

    @InjectMocks
    private static MenuController menuController;

    private static List<List<Element>> sampleTableData;

    @BeforeEach
    void setup() {
        System.setOut(new PrintStream(outputStreamCaptor));
        sampleTableData = new ArrayList<>();
        List<Element> innerList = new ArrayList<>();
        innerList.add(new Element("aaa", "bbb"));
        sampleTableData.add(innerList);
        mockTableService = mock(TableServiceImpl.class);
        menuController = new MenuController(mockTableService, mockFileIOUtil, sampleTableData);
    }

    @AfterEach
    void cleanUp() {
        System.setOut(standardOut);
    }

    @Test
    void testRun_SearchAndExit() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("-> "))
                    .thenReturn("search", "x");
            menuController.run();
            verify(mockTableService, times(1)).search(sampleTableData);
            mockInputUtil.verify(InputUtil::closeScanner, times(1));
            mockInputUtil.verify(() -> InputUtil.getStringInput("-> "), times(2));
            assertTrue(outputStreamCaptor.toString().trim().contains("Goodbye!"));
        }
    }

    @Test
    void testEditAndExit() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("-> "))
                    .thenReturn("edit", "x");
            List<List<Element>> editedTable = new ArrayList<>();
            when(mockTableService.edit(sampleTableData)).thenReturn(editedTable);
            menuController.run();
            verify(mockTableService, times(1)).edit(sampleTableData);
            verify(mockTableService, times(1)).printTable(editedTable);
            verify(mockFileIOUtil, times(1)).writeFile(editedTable);
            mockInputUtil.verify(InputUtil::closeScanner, times(1));
            mockInputUtil.verify(() -> InputUtil.getStringInput("-> "), times(2));
            assertTrue(outputStreamCaptor.toString().trim().contains("Goodbye!"));
        }
    }

    @Test
    void testAddRowAndExit() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("-> "))
                    .thenReturn("add_row", "x");
            List<List<Element>> addedRowTable = new ArrayList<>();
            when(mockTableService.addRow(sampleTableData)).thenReturn(addedRowTable);
            menuController.run();
            verify(mockTableService, times(1)).addRow(sampleTableData);
            verify(mockTableService, times(1)).printTable(addedRowTable);
            verify(mockFileIOUtil, times(1)).writeFile(addedRowTable);
            mockInputUtil.verify(InputUtil::closeScanner, times(1));
            mockInputUtil.verify(() -> InputUtil.getStringInput("-> "), times(2));
            assertTrue(outputStreamCaptor.toString().trim().contains("Goodbye!"));
        }
    }

    @Test
    void testSortAndExit() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("-> "))
                    .thenReturn("sort", "x");
            List<List<Element>> sortedRowTable = new ArrayList<>();
            when(mockTableService.sort(sampleTableData)).thenReturn(sortedRowTable);
            menuController.run();
            verify(mockTableService, times(1)).sort(sampleTableData);
            verify(mockTableService, times(1)).printTable(sortedRowTable);
            verify(mockFileIOUtil, times(1)).writeFile(sortedRowTable);
            mockInputUtil.verify(InputUtil::closeScanner, times(1));
            mockInputUtil.verify(() -> InputUtil.getStringInput("-> "), times(2));
            assertTrue(outputStreamCaptor.toString().trim().contains("Goodbye!"));
        }
    }

    @Test
    void testPrintAndExit() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("-> "))
                    .thenReturn("print", "x");
            menuController.run();
            verify(mockTableService, times(1)).printTable(sampleTableData);
            mockInputUtil.verify(InputUtil::closeScanner, times(1));
            mockInputUtil.verify(() -> InputUtil.getStringInput("-> "), times(2));
            assertTrue(outputStreamCaptor.toString().trim().contains("Goodbye!"));
        }
    }

    @Test
    void testResetAndExit() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("-> "))
                    .thenReturn("reset", "x");
            List<List<Element>> newTable = new ArrayList<>();
            menuController.run();
//            when(mockTableService.reset()).thenReturn(newTable);
            verify(mockTableService, times(1)).reset();
            verify(mockFileIOUtil, times(1)).writeFile(newTable);
            verify(mockTableService, times(1)).printTable(sampleTableData);
            mockInputUtil.verify(InputUtil::closeScanner, times(1));
            mockInputUtil.verify(() -> InputUtil.getStringInput("-> "), times(2));
            assertTrue(outputStreamCaptor.toString().trim().contains("Goodbye!"));
        }
    }

    @Test
    void testRun_withInvalidInput() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("-> "))
                    .thenReturn("corgi", "x");

            menuController.run();
            verifyNoInteractions(mockTableService);
            mockInputUtil.verify(InputUtil::closeScanner, times(1));
            mockInputUtil.verify(() -> InputUtil.getStringInput("-> "), times(2));
            assertTrue(outputStreamCaptor.toString().trim().contains("Goodbye!"));
        }
    }
}
