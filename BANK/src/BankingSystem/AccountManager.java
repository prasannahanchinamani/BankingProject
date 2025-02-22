package BankingSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {
    private Connection connection;
    private Scanner scanner;

    AccountManager(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    // Credit Money
    public void credit_money(long ACC_NO) throws SQLException {
        scanner.nextLine();
        System.out.print("Enter Amount: ");
        double AMOUNT = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String SECURITY_PIN = scanner.nextLine();
        
        try {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM Account WHERE ACC_NO = ? AND SECURITY_PIN = ?");
            ps.setLong(1, ACC_NO);
            ps.setString(2, SECURITY_PIN);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String credit_query = "UPDATE Account SET BALANCE = BALANCE + ? WHERE ACC_NO = ?";
                PreparedStatement updateStatement = connection.prepareStatement(credit_query);
                updateStatement.setDouble(1, AMOUNT);
                updateStatement.setLong(2, ACC_NO);
                int rowsAffected = updateStatement.executeUpdate();
                
                if (rowsAffected > 0) {
                    double newBalance = rs.getDouble("BALANCE") + AMOUNT;
                    System.out.println("Rs." + AMOUNT + " Credited Successfully!");
                    logTransaction(ACC_NO, "CREDIT", AMOUNT, newBalance);
                    connection.commit();
                } else {
                    System.out.println("Transaction Failed!");
                    connection.rollback();
                }
                updateStatement.close();
            } else {
                System.out.println("Invalid Security Pin!");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // Debit Money
    public void debit_money(long ACC_NO) throws SQLException {
        scanner.nextLine();
        System.out.print("Enter Amount: ");
        double AMOUNT = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String SECURITY_PIN = scanner.nextLine();
        
        try {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM Account WHERE ACC_NO = ? AND SECURITY_PIN = ?");
            ps.setLong(1, ACC_NO);
            ps.setString(2, SECURITY_PIN);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                double current_balance = rs.getDouble("BALANCE");
                if (AMOUNT <= current_balance) {
                    String debit_query = "UPDATE Account SET BALANCE = BALANCE - ? WHERE ACC_NO = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(debit_query);
                    updateStatement.setDouble(1, AMOUNT);
                    updateStatement.setLong(2, ACC_NO);
                    int rowsAffected = updateStatement.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        double newBalance = current_balance - AMOUNT;
                        System.out.println("Rs." + AMOUNT + " Debited Successfully.");
                        logTransaction(ACC_NO, "DEBIT", AMOUNT, newBalance);
                        connection.commit();
                    } else {
                        System.out.println("Transaction Failed!");
                        connection.rollback();
                    }
                    updateStatement.close();
                } else {
                    System.out.println("Insufficient Balance!");
                }
            } else {
                System.out.println("Invalid Security Pin!");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // Transfer Money
    public void transfer_money(long sender_ACC_NO) throws SQLException {
        scanner.nextLine();
        System.out.print("Enter Receiver's Account Number: ");
        long receiver_ACC_NO = scanner.nextLong();
        System.out.print("Enter Amount: ");
        double AMOUNT = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String SECURITY_PIN = scanner.nextLine();
        
        try {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM Account WHERE ACC_NO = ? AND SECURITY_PIN = ?");
            ps.setLong(1, sender_ACC_NO);
            ps.setString(2, SECURITY_PIN);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double senderBalance = rs.getDouble("BALANCE");

                // Check receiver account
                PreparedStatement ps2 = connection.prepareStatement(
                    "SELECT * FROM Account WHERE ACC_NO = ?");
                ps2.setLong(1, receiver_ACC_NO);
                ResultSet rs2 = ps2.executeQuery();

                if (rs2.next() && sender_ACC_NO != receiver_ACC_NO) {
                    if (senderBalance >= AMOUNT) {
                        // Deduct from sender
                        PreparedStatement updateSender = connection.prepareStatement(
                            "UPDATE Account SET BALANCE = BALANCE - ? WHERE ACC_NO = ?");
                        updateSender.setDouble(1, AMOUNT);
                        updateSender.setLong(2, sender_ACC_NO);
                        updateSender.executeUpdate();

                        // Add to receiver
                        PreparedStatement updateReceiver = connection.prepareStatement(
                            "UPDATE Account SET BALANCE = BALANCE + ? WHERE ACC_NO = ?");
                        updateReceiver.setDouble(1, AMOUNT);
                        updateReceiver.setLong(2, receiver_ACC_NO);
                        updateReceiver.executeUpdate();

                        connection.commit();
                        System.out.println("Rs." + AMOUNT + " Transferred Successfully!");

                        // Log Transactions
                        logTransaction(sender_ACC_NO, "TRANSFER OUT", AMOUNT, senderBalance - AMOUNT);
                        logTransaction(receiver_ACC_NO, "TRANSFER IN", AMOUNT, rs2.getDouble("BALANCE") + AMOUNT);

                        updateSender.close();
                        updateReceiver.close();
                    } else {
                        System.out.println("Insufficient Balance");
                    }
                } else {
                    System.out.println("Invalid Receiver Account Number");
                }
                rs2.close();
                ps2.close();
            } else {
                System.out.println("Invalid Security Pin");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // Get Balance
    public void getBalance(long ACC_NO) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT BALANCE FROM Account WHERE ACC_NO = ?");
            ps.setLong(1, ACC_NO);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("BALANCE");
                System.out.println("Current Balance: Rs." + balance);
            } else {
                System.out.println("Account Not Found!");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error retrieving balance: " + e.getMessage());
        }
    }

    // Log Transaction
    private void logTransaction(long ACC_NO, String type, double amount, double balanceAfter) {
        String log_query = "INSERT INTO TRANSACTION_HISTORY (ACC_NO, TYPE, AMOUNT, BALANCE_AFTER) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(log_query);
            ps.setLong(1, ACC_NO);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setDouble(4, balanceAfter);
            ps.executeUpdate();
            ps.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
 // View Transaction History
    public void viewTransactionHistory(long ACC_NO) {
        String query = "SELECT * FROM TRANSACTION_HISTORY WHERE ACC_NO = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setLong(1, ACC_NO);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nTransaction History\n(Save money):");
            while (rs.next()) {
                System.out.println("Type: " + rs.getString("TYPE") +
                        ", Amount: " + rs.getDouble("AMOUNT") +
                        ", Balance After: " + rs.getDouble("BALANCE_AFTER"));
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error fetching transaction history: " + e.getMessage());
        }
    }

}

