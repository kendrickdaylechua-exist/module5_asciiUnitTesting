package com.exist;

import java.util.InputMismatchException;
import java.util.Scanner;

class InputUtil {
    private static Scanner scanner = new Scanner(System.in);

    public static String getStringInput(String printOut) {
        String userInput;
        while (true) {
            try {
                System.out.print(printOut);
                userInput = scanner.nextLine();
                if (userInput.isEmpty()) {
                    System.err.print("Please input something... ");
                } else break;
            } catch (NullPointerException e) {
                System.err.println("A required object was null. Please try again...");
            } catch (Exception e) {
                System.err.println("Something went wrong...");
            }
        }
        return userInput;
    }

    public static int getIntInput(String printOut) {
        int userInput;

        while (true) {
            try {
                System.out.print(printOut);
                userInput = scanner.nextInt();
                scanner.nextLine();
                return userInput;
            } catch (InputMismatchException e) {
                System.err.print("Your input is not a number. Please try again...");
                scanner.nextLine();
            } catch (NullPointerException e) {
                System.err.println("A required object was null. Please try again...");
                scanner.nextLine();
            } catch (Exception e) {
                System.err.println("Something went wrong...");
            }
        }
    }

    public static void closeScanner() {
        scanner.close();
    }
}
