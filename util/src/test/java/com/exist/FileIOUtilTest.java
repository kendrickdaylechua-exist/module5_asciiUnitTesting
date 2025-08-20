package com.exist;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FileIOUtilTest {
    private final PrintStream standardOut = System.out;
    private final PrintStream standardError = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();

    private List<List<Element>> sampleTableData = new ArrayList<>();
    private String[] sampleArgs;
    private static File tempFile;

    private FileIOUtil realUtil;

    @BeforeAll
    static void createSampleTxtFile() throws IOException {
        tempFile = File.createTempFile("sample", ".txt"); //located in User\AppData\Local\Temp\sample.txt (DELETE AFTER)
        FileUtils.writeLines(tempFile, StandardCharsets.UTF_8.name(),
                List.of("Ākey1Ćvalue1Ą", "Ākey2Ćvalue2Ą"));
    }

    @BeforeEach
    void setup() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errorStreamCaptor));
        realUtil = spy(new FileIOUtil(sampleArgs));
    }

    @AfterEach
    void cleanup() {
        sampleTableData.clear();
        System.setOut(standardOut);
        System.setErr(standardError);
    }

    @Test
    void testAnalyzeFileValid() {
        String sampleLine = "Ākey1Ćvalue1ĄĀkey2Ćvalue2Ą";
        realUtil.analyzeFile(sampleLine);
        sampleTableData = realUtil.getTable();
        assertEquals("key1", sampleTableData.get(0).get(0).getKey());
        assertEquals("value1", sampleTableData.get(0).get(0).getValue());
        assertEquals("key2", sampleTableData.get(0).get(1).getKey());
        assertEquals("value2", sampleTableData.get(0).get(1).getValue());
    }

    @Test
    void testAnalyzeFileIfNoMiddleDelimiter() {
        String sampleLine = "Ākey1Ćvalue1ĄĀkeyOnlyĄ";
        realUtil.analyzeFile(sampleLine);
        sampleTableData = realUtil.getTable();
        assertEquals("key1", sampleTableData.get(0).get(0).getKey());
        assertEquals("value1", sampleTableData.get(0).get(0).getValue());
        assertEquals("keyOnly", sampleTableData.get(0).get(1).getKey());
        assertEquals("", sampleTableData.get(0).get(1).getValue());
    }

    @Test
    void testAnalyzeFileIfEmpty() {
        String sampleLine = "";
        realUtil.analyzeFile(sampleLine);
        sampleTableData = realUtil.getTable();
        assertTrue(sampleLine.isEmpty());
    }

    @Test
    void testNoEndDelimiterAtTheEnd() {
        String sampleLine = "Ākey1Ćvalue1ĄĀkey2Ćvalue2";
        realUtil.analyzeFile(sampleLine);
        sampleTableData = realUtil.getTable();
        assertEquals("key1", sampleTableData.get(0).get(0).getKey());
        assertEquals("value1", sampleTableData.get(0).get(0).getValue());
        assertEquals("key2", sampleTableData.get(0).get(1).getKey());
        assertEquals("value2", sampleTableData.get(0).get(1).getValue());
    }

    @Test
    void testNoEndDelimiterButStartDelimiterIsSeen() {
        String sampleLine = "Ākey1Ćvalue1ĀstillValue1Ą";
        realUtil.analyzeFile(sampleLine);
        sampleTableData = realUtil.getTable();
        assertEquals("key1", sampleTableData.get(0).get(0).getKey());
        assertEquals("value1ĀstillValue1", sampleTableData.get(0).get(0).getValue());
    }

    @Test
    void testNoStartDelimiterForFirstElement() {
        String sampleLine = "notKeyĆnotValueĀkey1Ćvalue2Ą";
        realUtil.analyzeFile(sampleLine);
        sampleTableData = realUtil.getTable();
        assertEquals("key1", sampleTableData.get(0).get(0).getKey());
        assertEquals("value2", sampleTableData.get(0).get(0).getValue());
    }

    @Test
    void testNoStartDelimiterAtAll() {
        String sampleLine = "notValidKeyĆnotValidValueĄ";
        realUtil.analyzeFile(sampleLine);
        sampleTableData = realUtil.getTable();
        assertTrue(sampleTableData.isEmpty());
    }

    @Test
    void testFileExistsOutsideJar() {
        boolean result = realUtil.readFile(tempFile.getAbsolutePath());

        assertTrue(result);
        assertEquals(tempFile.getAbsolutePath(), realUtil.getTrueFile());
    }

    @Test
    void testFileExistsInsideJar() {

        boolean result = realUtil.readFile("testInside.txt");

        assertTrue(result);
        assertEquals("testInside.txt", realUtil.getTrueFile());
    }

    @Test
    void testFileNotFound() {
        realUtil.setFileInnerPrefix("");
        boolean result = realUtil.readFile("random.txt");
        assertFalse(result);
        assertEquals("File not found in JAR or outside: random.txt", errorStreamCaptor.toString().trim());
    }

    @Test
    void testCheckOneArgsValid() {
        sampleArgs = new String[] { tempFile.getAbsolutePath() };
        realUtil = spy(new FileIOUtil(sampleArgs));

        realUtil.checkArgs();

        assertEquals(tempFile.getAbsolutePath(), realUtil.getTrueFile());
        assertEquals("key1", realUtil.getTable().get(0).get(0).getKey());
        assertEquals("value1", realUtil.getTable().get(0).get(0).getValue());
        assertEquals("key2", realUtil.getTable().get(1).get(0).getKey());
        assertEquals("value2", realUtil.getTable().get(1).get(0).getValue());
    }

    @Test
    void testCheckTwoArgsValid() {
        sampleArgs = new String[]{
                tempFile.getAbsolutePath(),
                "not_valid.txt"
        };
        realUtil = spy(new FileIOUtil(sampleArgs));
        realUtil.setFileInnerPrefix("testInside");
        realUtil.checkArgs();
        assertEquals("Only 1 file at a time!" +
                System.lineSeparator() +
                "File inside JAR read successfully!", outputStreamCaptor.toString().trim());

        assertEquals("key1", realUtil.getTable().get(0).get(0).getKey());
        assertEquals("value1", realUtil.getTable().get(0).get(0).getValue());
        assertEquals("key2", realUtil.getTable().get(1).get(0).getKey());
        assertEquals("value2", realUtil.getTable().get(1).get(0).getValue());
    }

    @Test
    void testNoArguments() {
        sampleArgs = new String[0];
        realUtil = new FileIOUtil(sampleArgs);
        realUtil.setFileInnerPrefix("testInside");
        realUtil.checkArgs();
        assertEquals("testInside.txt", realUtil.getTrueFile());
        assertEquals("key1", realUtil.getTable().get(0).get(0).getKey());
        assertEquals("value1", realUtil.getTable().get(0).get(0).getValue());
        assertEquals("key2", realUtil.getTable().get(1).get(0).getKey());
        assertEquals("value2", realUtil.getTable().get(1).get(0).getValue());
    }

    @Test
    void testFileNotFoundAnywhereThenInputValidOutsideFilename() {
        try (MockedStatic<InputUtil> mockInputUtil = mockStatic(InputUtil.class)) {
            mockInputUtil.when(() -> InputUtil.getStringInput("Please input valid filename: "))
                    .thenReturn(tempFile.getAbsolutePath());
            sampleArgs = new String[]{"not_valid.txt"};
            realUtil = new FileIOUtil(sampleArgs);
            realUtil.setFileInnerPrefix("not_valid.txt");
            realUtil.checkArgs();
            assertEquals(tempFile.getAbsolutePath(), realUtil.getTrueFile());
            assertEquals("key1", realUtil.getTable().get(0).get(0).getKey());
            assertEquals("value1", realUtil.getTable().get(0).get(0).getValue());
            assertEquals("key2", realUtil.getTable().get(1).get(0).getKey());
            assertEquals("value2", realUtil.getTable().get(1).get(0).getValue());
        }
    }

}
