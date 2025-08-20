package com.exist;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

public class PrintTest {
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private List<List<Element>> sampleTableData;

    private final TableServiceImpl realService = new TableServiceImpl();

    @BeforeEach
    void createSampleTable() {
        System.setOut(new PrintStream(outputStreamCaptor));
        sampleTableData = new ArrayList<>();
        List<Element> innerList1 = new ArrayList<>();
        List<Element> innerList2 = new ArrayList<>();
        innerList1.add(new Element("aaa", "bbb"));
        innerList1.add(new Element("bbb", "ccc"));
        innerList2.add(new Element("qwer", "asdf"));
        sampleTableData.add(innerList1);
        sampleTableData.add(innerList2);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    void testPrintReturnsSameTable() {
        realService.printTable(sampleTableData);
        String expectedOutput =
                """
                        ----------
                        aaa , bbb   bbb , ccc  \s
                        
                        qwer , asdf  \s
                        
                        """;

        // Normalize both strings: replace CRLF with LF and trim trailing whitespace/newlines
        String actual = outputStreamCaptor.toString()
                .replace("\r\n", "\n")
                .stripTrailing();

        String expected = expectedOutput
                .replace("\r\n", "\n")
                .stripTrailing();

        assertEquals(expected, actual);
    }
}
