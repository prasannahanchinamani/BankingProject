package BankingSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Account {
    private Connection connection;
    private Scanner scanner;

    public Account(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public long open_account(String Email) {
        if (!account_exits(Email)) {
            String open_query = "INSERT INTO account(ACC_NO, FULL_NAME, EMAIL, BALANCE, SECURITY_PIN) VALUES(?,?,?,?,?)";
            scanner.nextLine();
            System.out.print("Enter Full Name: ");
            String FULL_NAME = scanner.nextLine();
            System.out.print("Enter Initial Amount: ");
            double BALANCE = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Enter Security Pin: ");
            String SECURITY_PIN = scanner.nextLine();
            
            try {
                long ACC_NO = generate_account_number();
                PreparedStatement preparedstatement = connection.prepareStatement(open_query);
                preparedstatement.setLong(1, ACC_NO);
                preparedstatement.setString(2, FULL_NAME);
                preparedstatement.setString(3, Email);
                preparedstatement.setDouble(4, BALANCE);
                preparedstatement.setString(5, SECURITY_PIN);
                int rowaffected = preparedstatement.executeUpdate();
                if (rowaffected > 0) {
                    System.out.println("Account Created Successfully!");
                    return ACC_NO;
                } else {
                    throw new RuntimeException("OOPS! Account creation failed. Try Again.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Account already exists!");
    }

    public long getAccount_number(String Email) {
        String query = "SELECT ACC_NO FROM ACCOUNT WHERE EMAIL = ?";
        try {
            PreparedStatement preparedstatement = connection.prepareStatement(query);
            preparedstatement.setString(1, Email);
            ResultSet resultset = preparedstatement.executeQuery();
            if (resultset.next()) {
                return resultset.getLong("ACC_NO");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Account number does not exist!");
    }

    private long generate_account_number() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultset = statement.executeQuery("SELECT ACC_NO FROM ACCOUNT ORDER BY ACC_NO DESC LIMIT 1");
            if (resultset.next()) {
                long Last_ACC_NO = resultset.getLong("ACC_NO");
                return Last_ACC_NO + 2;
            } else {
                return 10000100;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 10000100;
    }

    public boolean account_exits(String Email) {
        String query = "SELECT * FROM ACCOUNT WHERE EMAIL = ?";
        try {
            PreparedStatement preparedstatement = connection.prepareStatement(query);
            preparedstatement.setString(1, Email);
            ResultSet resultset = preparedstatement.executeQuery();
            if (resultset.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
