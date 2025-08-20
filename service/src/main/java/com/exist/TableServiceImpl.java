package com.exist;

import java.util.List;
import java.util.ArrayList;
import java.lang.Exception;
import java.util.InputMismatchException;
import java.util.*;

public class TableServiceImpl implements TableService{

	public void printTable(List<List<Element>> tableData) {
		String key;
		String value;
		System.out.println("----------");
        for (List<Element> tableDatum : tableData) {
            for (Element element : tableDatum) {
                key = element.getKey();
                value = element.getValue();
                System.out.print(key + " , " + value + "   ");
            }
            System.out.println("\n");
        }
	}

	public void search(List<List<Element>> tableData) {
		boolean exists = false;
		String searchString;

		searchString = InputUtil.getStringInput("Search: ");

		for (int r = 0; r < tableData.size(); r++) {
  			for (int col = 0; col < tableData.get(r).size(); col++) {
    			int fromIndex = 0;
		        int keyOccurence = 0;
		        int valueOccurence = 0;
		        boolean keyFound = false;
		        boolean valueFound = false;
		        
		        while ((fromIndex = tableData.get(r).get(col).getKey().indexOf(searchString, fromIndex)) != -1) {
		            keyOccurence++;
		            fromIndex++;
		            keyFound = true;
		        }

		        while ((fromIndex = tableData.get(r).get(col).getValue().indexOf(searchString, fromIndex)) != -1) {
		        	valueOccurence++;
		            fromIndex++;
		            valueFound = true;
		        }

		        if (keyFound && !valueFound) {
		        	System.out.println(keyOccurence + " <" + searchString + "> at key of [" + r + ", "+ col + "]");
		        	exists = true;
		        } else if (valueFound && !keyFound) {
		        	System.out.println(valueOccurence + " <" + searchString + "> at value of [" + r + ", "+ col + "]");
		        	exists = true;
		        } else if (valueFound) {
		        	System.out.println(keyOccurence + " <" + searchString + "> at key and " + valueOccurence + " <" + searchString + "> at value of [" + r + ", "+ col + "]");
					exists = true;
		        }
  			}
		}
		if (!exists) {
			System.out.println("No result found");
		}
	}

	public List<List<Element>> edit(List<List<Element>> tableData) {
		if (tableData.isEmpty()) {
			System.err.println("Table is empty. You cannot edit anything!");
			return tableData;
		}

		boolean isInvalid = true;
		String userInputRowColumn;
		List<Integer> rowColumn;
		
		while(isInvalid) {
			userInputRowColumn = InputUtil.getStringInput("Edit [row,column]: ").toLowerCase();
			try {
				rowColumn = editCheckInput(userInputRowColumn, tableData);
				int editRow = rowColumn.get(0);
				int editColumn = rowColumn.get(1);
				editCheckKeyValue(editRow, editColumn, tableData);
				isInvalid = false;

			} catch (InvalidFormatException e) {
				System.err.println(e);
				System.out.println("Input must be in the format of [row,column]");

			}catch (NumberFormatException e) {
				System.err.println("Invalid input: " + e.getMessage());

			} catch (IndexOutOfBoundsException e) {
				System.err.println("Index Out Of Bounds! Please input [row, column] again: ");
			}
		}
		return tableData;
	}

	public List<Integer> editCheckInput(String userInputRowColumn, List<List<Element>> tableData) throws InputMismatchException, IndexOutOfBoundsException, InvalidFormatException{
		int editRow = 0;
		int editColumn = 0;

		if (userInputRowColumn.contains(",") && userInputRowColumn.charAt(0) == '[' && userInputRowColumn.charAt(userInputRowColumn.length() - 1) == ']') {
			String[] parts = userInputRowColumn.substring(1, userInputRowColumn.length() - 1).split(",");
			editRow = Integer.parseInt(parts[0]);
			editColumn = Integer.parseInt(parts[1]);

			if (editRow >= tableData.size() || editColumn >= tableData.get(editRow).size()) {
				throw new IndexOutOfBoundsException("Element does not exists!");
			}
		} else {
			throw new InvalidFormatException("Invalid Input!");
		}
		return Arrays.asList(editRow, editColumn);
	}

	public void editCheckKeyValue(int editRow, int editColumn, List<List<Element>> tableData) {
		boolean isInvalid = true;
		String editContent;

		while (isInvalid) {
			String option = InputUtil.getStringInput("Key, Value, or Both?: ").toLowerCase();

			switch (option) {
				case "key" -> {
					editContent = InputUtil.getStringInput("Input new key: ");
					tableData.get(editRow).get(editColumn).setKey(editContent);
					isInvalid = false;
				}
				case "value" -> {
					editContent = InputUtil.getStringInput("Input new value: ");
					tableData.get(editRow).get(editColumn).setValue(editContent);
					isInvalid = false;
				}
				case "both" -> {
					editContent = InputUtil.getStringInput("Input both key and value: ");
					tableData.get(editRow).get(editColumn).setKey(editContent);
					tableData.get(editRow).get(editColumn).setValue(editContent);
					isInvalid = false;
				}
				default -> System.out.println("Invalid Input");
			}
		}
	}

	public List<List<Element>> addRow(List<List<Element>> tableData) {
		int userInputColumn;
		while(true) {
			userInputColumn = InputUtil.getIntInput("No. of Cells: ");
			if (userInputColumn <= 0) {
				System.out.println("The number of cells cannot be 0 or less. Please try again...");
			} else break;
		}
		while(true) {
			try {
				int userInputRow = InputUtil.getIntInput("Row Index (Table Size: " + tableData.size() + "): ");
				tableData.add(userInputRow, asciiGenerator(userInputColumn));
				break;
			} catch (IndexOutOfBoundsException e) {
				System.out.println("Index Out of Bound! Please try again...");
			}
		}
		return tableData;
	}

	public List<List<Element>> sort(List<List<Element>> tableData) {
		if (tableData.isEmpty()) {
			System.err.println("Table is empty. You cannot edit anything!");
			return tableData;
		}

		int sortRow;
		boolean valid = false;
		String sortingOption;

		while (!valid) {
			try{
				String userInput = InputUtil.getStringInput("Row to sort: ").toLowerCase().trim().replaceAll("\\s","");

				List<Object> rowAndChoice = sortCheckInput(userInput, tableData);
				sortRow = (int) rowAndChoice.get(0);
				sortingOption = (String) rowAndChoice.get(1);

				valid = checkSortingOption(sortRow, sortingOption, tableData);

			} catch (InputMismatchException e) {
				System.err.println("Invalid input: " + e.getMessage());
				System.out.println("Input should be x - <asc,desc>");

			} catch (NumberFormatException e) {
				System.err.println(e.getMessage() + "is not a number");

			} catch (IndexOutOfBoundsException e) {
				System.err.println("Index Out Of Bounds!");

			} catch (Exception e) {
				System.err.println("Something went wrong");
				e.printStackTrace();
			}
		}
		return tableData;
	}

	public List<Object> sortCheckInput(String userInput, List<List<Element>> tableData) throws InputMismatchException, NumberFormatException, IndexOutOfBoundsException {
		int sortRow;
		String sortingOption;
		if (userInput.contains("-")) {
			int chopIndex = userInput.indexOf("-");

			String rowString = userInput.substring(0, chopIndex);
			sortRow = Integer.parseInt(rowString);

			sortingOption = userInput.substring(chopIndex+1);
		} else {
			throw new InputMismatchException();
		}

		if (sortRow >= tableData.size()) {
			throw new IndexOutOfBoundsException("Please try again...");
		}
		return Arrays.asList(sortRow, sortingOption);
	}

	public boolean checkSortingOption(int sortRow, String sortingOption, List<List<Element>> tableData) {
		List<Element> sortedRow = tableData.get(sortRow);
		switch (sortingOption) {
			case "desc" -> {
	            sortedRow.sort((e1, e2) -> {
                    String combined1 = e1.getKey() + e1.getValue();
                    String combined2 = e2.getKey() + e2.getValue();
                    return combined2.compareTo(combined1); // descending
                });
				tableData.set(sortRow, sortedRow);
				return true;
			}
			case "asc" -> {
	            sortedRow.sort((e1, e2) -> {
                    String combined1 = e1.getKey() + e1.getValue();
                    String combined2 = e2.getKey() + e2.getValue();
                    return combined1.compareTo(combined2); // ascending
                });

				tableData.set(sortRow, sortedRow);
				return true;
			}
			default -> {
				System.out.println("Invalid Input!");
				return false;
			}
		}
	}

	public List<List<Element>> reset() {
		List<List<Element>> newTableData = new ArrayList<>();
		List<Integer> rowColumn = inputRowColumn();
		for (int r = 0; r < rowColumn.get(0); r++) {
			newTableData.add(asciiGenerator(rowColumn.get(1)));
		}
		return newTableData;
	}

	public List<Integer> inputRowColumn() {
		boolean running = true;
		String delimiter = "x";
		int row = 0;
		int column = 0;

		while (running) {
			String userInput = InputUtil.getStringInput("Input table dimension: ").toLowerCase();

			if (userInput.contains(delimiter)) {
				try {
					String[] parts = userInput.split(delimiter);
					row = Integer.parseInt(parts[0]);
					column = Integer.parseInt(parts[1]);
					if (row <= 0 || column <= 0) {
						throw new ZeroAndNegativeException("Invalid Input!");
					}
					running = false;
				} catch (ZeroAndNegativeException e) {
					System.err.println(e);
					System.out.println("Input cannot be zero or negative");
				} catch (NumberFormatException e) {
					System.err.println("Invalid number format: " + e.getMessage());
				}
			} else {
				System.out.println("Input must be in the format of NxN");
			}
		}
		return Arrays.asList(row, column);
	}

	public List<Element> asciiGenerator(int column) {
		Random random = new Random();
		List<Element> innerList = new ArrayList<>();
		for (int col = 0; col < column; col++) {
			StringBuilder key = new StringBuilder();
			StringBuilder value = new StringBuilder();
			for (int i = 0; i < 3; i++) {
				char c = (char) random.nextInt(32, 127);
				key.append(c);
				c = (char) random.nextInt(32, 127);
				value.append(c);
			}
			Element map = new Element(key.toString(), value.toString());
			innerList.add(map);
		}
		return innerList;
	}
}