package com.exist;

import java.util.LinkedList;
import java.util.List;

public class MenuController {
    private TableServiceImpl tableService = new TableServiceImpl();
    protected FileIOUtil fileIO;
    protected List<List<Element>> tableData;

    public MenuController(String[] args) {
        fileIO = new FileIOUtil(args);
        fileIO.checkArgs();
        tableData = fileIO.getTable();
        if (tableData == null) tableData = new LinkedList<>();
        tableService.printTable(tableData);
    }

    //For testing purposes
    public MenuController(TableServiceImpl tableService, FileIOUtil fileIO, List<List<Element>> tableData) {
        this.tableService = tableService;
        this.fileIO = fileIO;
        this.tableData = tableData;
    }

    public void run() {
        boolean running = true;

        while(running) {
            System.out.println("============================");
            System.out.println("[ search ] - Search");
            System.out.println("[ edit ] - Edit");
            System.out.println("[ add_row ] - Add Row");
            System.out.println("[ sort ] - Sort");
            System.out.println("[ print ] - Print");
            System.out.println("[ reset ] - Reset");
            System.out.println("[ x ] - Exit");
            System.out.println("============================");

            String option = InputUtil.getStringInput("-> ");
            switch (option) {
                case "search" -> tableService.search(tableData);
                case "edit" -> {
                    tableData = tableService.edit(tableData);
                    tableService.printTable(tableData);
                    fileIO.writeFile(tableData);
                }
                case "add_row" -> {
                    tableData = tableService.addRow(tableData);
                    tableService.printTable(tableData);
                    fileIO.writeFile(tableData);
                }
                case "sort" -> {
                    tableData = tableService.sort(tableData);
                    tableService.printTable(tableData);
                    fileIO.writeFile(tableData);
                }
                case "print" -> tableService.printTable(tableData);
                case "reset" -> {
                    tableData.clear();
                    tableData = tableService.reset();
                    tableService.printTable(tableData);
                    fileIO.writeFile(tableData);
                }
                case "x" -> running = false;
                default -> System.out.println("Invalid Input");
            }
        }
        InputUtil.closeScanner();
        System.out.println("Goodbye!");
    }
}
