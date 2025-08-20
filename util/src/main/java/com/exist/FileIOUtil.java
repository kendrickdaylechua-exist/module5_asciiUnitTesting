package com.exist;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;

public class FileIOUtil {
	private final String[] args;
	private String input;
	private List<List<Element>> tableData = new ArrayList<>();
	private String trueFile;
	private String fileInnerPrefix = "TableDataInner";
	private static final String FILE_PREFIX = "TableData";
	private static final String FILE_SUFFIX = ".txt";

    public FileIOUtil(String[] args) {
		this.args = args;
	}

	public List<List<Element>> getTable() {
		return this.tableData;
	}

	public String getTrueFile() {
		return trueFile;
	}

	public void setFileInnerPrefix(String fileInnerPrefix) {
		this.fileInnerPrefix = fileInnerPrefix;
	}

	public void checkArgs() {
		boolean fileComplete = false;
		String fileName;

		if(args.length == 1) {
			fileName = args[0];
			fileComplete = readFile(fileName);
		} else if(args.length == 0) {
			System.out.println("No provided arguments");
			fileComplete = readFile(fileInnerPrefix +FILE_SUFFIX);
		} else {
			System.out.println("Only 1 file at a time!");
			fileComplete = readFile(fileInnerPrefix +FILE_SUFFIX);
		}

		while(!fileComplete) {
			fileName = InputUtil.getStringInput("Please input valid filename: ");
			fileComplete = readFile(fileName);
		}
	}

	public boolean readFile(String fileName) {
		try {
			File outsideFile = new File(fileName);
			if (outsideFile.exists()) {
				List<String> lines = FileUtils.readLines(outsideFile, StandardCharsets.UTF_8);
				lines.forEach(this::analyzeFile);
				System.out.println("File read successfully from outside JAR!");
				trueFile = fileName;
				return true;
			}

			try (InputStream in = FileIOUtil.class.getResourceAsStream("/" + fileInnerPrefix + FILE_SUFFIX)) {
				if (in == null) {
					System.err.println("File not found in JAR or outside: " + fileName);
					return false;
				}

				List<String> lines = IOUtils.readLines(in, StandardCharsets.UTF_8);
				lines.forEach(this::analyzeFile);
				System.out.println("File inside JAR read successfully!");
				trueFile = fileName;
				return true;
			}
		} catch(Exception e) {
			System.out.println("Something went wrong with the file. Please try again...");
			e.printStackTrace();
			return false;
		}
	}

	protected void analyzeFile(String dataLine) {
		List<Element> innerList = new ArrayList<>();

		String startDelimiter = "Ā";
		String middleDelimiter = "Ć";
		String endDelimiter = "Ą";

		int index = 0;
		while (index < dataLine.length()) {
			int start = dataLine.indexOf(startDelimiter, index);
			if (start == -1) break; // No more valid start delimiters

			int end = dataLine.indexOf(endDelimiter, start + 1);
			if (end == -1) {
				// No end delimiter found: take everything till the end of the string
				end = dataLine.length();
			}

			int middle = dataLine.indexOf(middleDelimiter, start + 1);

			String key;
			String value;

			if (middle == -1 || middle > end) {
				// No middle delimiter found or it comes after end
				key = dataLine.substring(start + 1, end);
				value = ""; // Or "" if preferred
			} else {
				key = dataLine.substring(start + 1, middle);
				value = dataLine.substring(middle + 1, end);
			}

			innerList.add(new Element(key, value));

			// Move index to the character after the end delimiter (or end of string)
			index = end + 1;
		}

		if (!innerList.isEmpty()) tableData.add(innerList);
	}

	public void writeFile(List<List<Element>> tableData) {
		this.tableData = tableData;
		try {
			File file = new File(FILE_PREFIX +FILE_SUFFIX);
			FileUtils.writeLines(file, StandardCharsets.UTF_8.name(), processText());
			System.out.println("File successfully written!");

		} catch(IOException e) {
			System.out.println("Cannot find filename");
		}
	}

	private ArrayList<String> processText() {
		String key;
		String value;
		ArrayList<String> texts = new ArrayList<>();
		for (int i = 0; i < tableData.size(); i++) {
			texts.add("");
			for (int j = 0; j < tableData.get(i).size(); j++) {
				String tempText = texts.get(i);
				key = tableData.get(i).get(j).getKey();
				value = tableData.get(i).get(j).getValue();
				tempText = tempText.concat("Ā" + key + "Ć" + value + "Ą");
				texts.set(i, tempText);
			}
		}
		return texts;
	}
}