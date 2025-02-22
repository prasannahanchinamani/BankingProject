package BankingSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class BankingApp {
    private static final String url = "jdbc:mysql://localhost:3306/banking_system";
    private static final String username = "root";
    private static final String password = "root";

    // ANSI Color Codes for Console
    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Scanner scanner = new Scanner(System.in);

            User user = new User(connection, scanner);
            Account accounts = new Account(connection, scanner);
            AccountManager accountManager = new AccountManager(connection, scanner);

            String Email;
            long ACC_NO;

            while (true) {
                System.out.println(BLUE + "\n===============================");
                System.out.println("    WELCOME TO BANKING SYSTEM");
                System.out.println("===============================" + RESET);
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print(YELLOW + "Enter your choice: " + RESET);

                int choice1 = 0;
                try {
                    choice1 = scanner.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println(RED + "Invalid input! Please enter a number." + RESET);
                    scanner.next(); // Clear the invalid input
                    continue;
                }

                switch (choice1) {
                    case 1:
                        user.Register();
                        break;
                    case 2:
                        Email = user.login();
                        if (Email != null) {
                            System.out.println(GREEN + "\nUser Logged In!" + RESET);

                            if (!accounts.account_exits(Email)) {
                                System.out.println(BLUE + "\n===============================");
                                System.out.println("    Account Options");
                                System.out.println("===============================" + RESET);
                                System.out.println("1. Open a new Bank Account");
                                System.out.println("2. Exit");
                                System.out.print(YELLOW + "Enter your choice: " + RESET);

                                if (scanner.nextInt() == 1) {
                                    ACC_NO = accounts.open_account(Email);
                                    if (ACC_NO != -1) {
                                        System.out.println(GREEN + "Account Created Successfully!" + RESET);
                                        System.out.println("Your Account Number is: " + ACC_NO);
                                    }
                                } else {
                                    break;
                                }
                            }

                            ACC_NO = accounts.getAccount_number(Email);
                            int choice2 = 0;
                            while (choice2 != 6) {
                                System.out.println(BLUE + "\n===============================");
                                System.out.println("    ACCOUNT DASHBOARD");
                                System.out.println("===============================" + RESET);
                                System.out.println("1. Debit Money");
                                System.out.println("2. Credit Money");
                                System.out.println("3. Transfer Money");
                                System.out.println("4. Check Balance");
                                System.out.println("5. View Transaction History");
                                System.out.println("6. Log Out");
                                System.out.print(YELLOW + "Enter your choice: " + RESET);

                                try {
                                    choice2 = scanner.nextInt();
                                } catch (InputMismatchException e) {
                                    System.out.println(RED + "Invalid input! Please enter a number." + RESET);
                                    scanner.next(); // Clear the invalid input
                                    continue;
                                }

                                switch (choice2) {
                                    case 1:
                                        accountManager.debit_money(ACC_NO);
                                        break;
                                    case 2:
                                        accountManager.credit_money(ACC_NO);
                                        break;
                                    case 3:
                                        accountManager.transfer_money(ACC_NO);
                                        break;
                                    case 4:
                                        accountManager.getBalance(ACC_NO);
                                        break;
                                    case 5:
                                        accountManager.viewTransactionHistory(ACC_NO);
                                        break;
                                    case 6:
                                        System.out.println(GREEN + "Logged Out Successfully!" + RESET);
                                        break;
                                    default:
                                        System.out.println(RED + "Enter a Valid Choice!" + RESET);
                                        break;
                                }
                            }
                        } else {
                            System.out.println(RED + "Incorrect Email or Password!" + RESET);
                        }
                        break;
                    case 3:
                        System.out.println(GREEN + "THANK YOU FOR USING BANKING SYSTEM!!!" + RESET);
                        System.out.println("Exiting System!");
                        return;
                    default:
                        System.out.println(RED + "Enter a Valid Choice!" + RESET);
                        break;
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println(RED + "MySQL Driver not found: " + e.getMessage() + RESET);
        } catch (SQLException e) {
            System.out.println(RED + "Database connection error: " + e.getMessage() + RESET);
        }
    }
}
