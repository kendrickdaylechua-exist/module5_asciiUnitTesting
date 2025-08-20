package com.exist;

import java.util.*;

interface TableService {
    void printTable(List<List<Element>> tableData);
    void search(List<List<Element>> tableData);
    List<List<Element>> edit(List<List<Element>> tableData);
    List<Integer> editCheckInput(String userInputRowColumn, List<List<Element>> tableData) throws InputMismatchException, IndexOutOfBoundsException;
    void editCheckKeyValue(int editRow, int editColumn, List<List<Element>> tableData);
    List<List<Element>> addRow(List<List<Element>> tableData);
    List<List<Element>> sort(List<List<Element>> tableData);
    boolean checkSortingOption(int sortRow, String sortingOption, List<List<Element>> tableData);
    List<List<Element>> reset();
    List<Integer> inputRowColumn();
    List<Element> asciiGenerator(int column);
}
