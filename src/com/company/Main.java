package com.company;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Main {
    private static final int max_login = 3;
    private static final int default_balance = 0;
    private static final String url = "jdbc:mysql://localhost:3306/banksystem";
    private static final String db_user = "root";
    private static final String db_password = "";

    public static void main(String[] args) {
        createTable();
        SwingUtilities.invokeLater(Main::createAndShowGUI); //تاجيل التنفيذ
    }

    private static void createTable() {
        try (Connection conn = DriverManager.getConnection(url, db_user, db_password);
             Statement stmt = conn.createStatement()) {
            String sql_table = "CREATE TABLE IF NOT EXISTS userss (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(255) NOT NULL UNIQUE," +
                    "password VARCHAR(255) NOT NULL," +
                    "balance INT NOT NULL" +
                    ")";
            stmt.executeUpdate(sql_table);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error creating table: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void addUserToDB(String username, String password) {
        String sql = "INSERT INTO userss (username, password, balance) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, db_user, db_password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setInt(3, default_balance);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "The new user has been added.\nYou can now log in.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding user: " + e.getMessage());
        }
    }

    private static boolean validateUser(String username) {
        String sql = "SELECT * FROM userss WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url, db_user, db_password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) { //استبدال القيم
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); //ترجع boolean سوا موجود او لا
            }
        } catch (SQLException e) { //اذا حصل خطي اثناء الاتصال بالداتا بيز
            e.printStackTrace(); //اطبع الخطئ الذي حدث
        }
        return false;
    }

    private static int validateUser(String username, String password) {
        String sql = "SELECT * FROM userss WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(url, db_user, db_password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id"); //يرجع ال id
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static void change(JFrame mainFrame, User user) {
        JDialog dialog = new JDialog(mainFrame, "Change Password", true); //للالغاء اضغط x
        JPanel panel = new JPanel(new GridLayout(4, 1)); // ترتيب العناصر
        JLabel oldPasswordLabel = new JLabel("Enter the old password:");
        JPasswordField oldPasswordText = new JPasswordField();
        JLabel newPasswordLabel = new JLabel("Enter the new password:");
        JPasswordField newPasswordText = new JPasswordField();
        JButton changeButton = new JButton("Change Password");
        JButton cancelButton = new JButton("Cancel");

        changeButton.setBackground(new Color(59, 89, 182));
        changeButton.setForeground(new Color(255, 255, 255));
        changeButton.setFocusPainted(false);

        cancelButton.setBackground(new Color(255, 0, 0));
        cancelButton.setForeground(new Color(255, 255, 255));
        cancelButton.setFocusPainted(false);

        changeButton.addActionListener(e -> {  //التنفيذ عند الضغط
            char[] oldPass = oldPasswordText.getPassword();
            if (!String.valueOf(oldPass).equals(user.getPassword())) {
                JOptionPane.showMessageDialog(null, "Wrong password. Please enter the correct password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            char[] newPass = newPasswordText.getPassword();
            user.setPassword(String.valueOf(newPass));
            if (updatePassword(user.getUsername(), String.valueOf(newPass))) {
                JOptionPane.showMessageDialog(null, "Password changed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to change password. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e ->
                { dialog.dispose(); }
        );

        panel.add(oldPasswordLabel);
        panel.add(oldPasswordText);
        panel.add(newPasswordLabel);
        panel.add(newPasswordText);
        panel.add(changeButton);
        panel.add(cancelButton);

        dialog.add(panel);
        dialog.setSize(300, 180);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true); //اظهار الواجهه
    }

    private static void createAndShowGUI() {
        JFrame mainFrame = new JFrame("ATM System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 300);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(230, 230, 250));

        JLabel titleLabel = new JLabel("ATM System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(25, 25, 112));

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10)); //مسافات افقيه و راسيه بعد العناصر
        buttonPanel.setBackground(new Color(230, 230, 250));

        JButton newUserButton = new JButton("New User");
        JButton loginButton = new JButton("Login");
        JButton exitButton = new JButton("Exit");

        newUserButton.setFont(new Font("Arial", Font.PLAIN, 18));
        loginButton.setFont(new Font("Arial", Font.PLAIN, 18));
        exitButton.setFont(new Font("Arial", Font.PLAIN, 18));

        newUserButton.setBackground(new Color(65, 105, 225));
        newUserButton.setForeground(new Color(255, 255, 255));
        loginButton.setBackground(new Color(65, 105, 225));
        loginButton.setForeground(new Color(255, 255, 255));
        exitButton.setBackground(new Color(220, 20, 60));
        exitButton.setForeground(new Color(255, 255, 255));

        newUserButton.setFocusPainted(false);
        loginButton.setFocusPainted(false);
        exitButton.setFocusPainted(false);

        newUserButton.addActionListener(e -> addUser(mainFrame));
        loginButton.addActionListener(e -> login(mainFrame));
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(newUserButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        mainFrame.add(panel);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private static void addUser(JFrame mainFrame) {
        JDialog dialog = new JDialog(mainFrame, "Add New User", true);
        dialog.setSize(350, 180);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(mainFrame);

        JPanel panel = new JPanel(new BorderLayout());

        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(240, 240, 240));

        JLabel nameLabel = new JLabel("Enter your name:");
        JTextField nameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Enter your password:");
        JPasswordField passwordField = new JPasswordField(15);

        JButton addButton = new JButton("Add User");
        JButton cancelButton = new JButton("Cancel");

        addButton.setBackground(new Color(0, 102, 204));
        addButton.setForeground(new Color(255,255,255));
        cancelButton.setBackground(new Color(255, 51, 51));
        cancelButton.setForeground(new Color(255,255,255));

        addButton.setFocusPainted(false);
        cancelButton.setFocusPainted(false);

        addButton.addActionListener(e -> {
            String username = nameField.getText().trim();
            String password = String.valueOf(passwordField.getPassword()).trim();

            if (!username.isEmpty() && !password.isEmpty()) {
                addUserToDB(username, password);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Invalid username or password. Please try again.");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        inputPanel.setBackground(new Color(240, 240, 240));
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private static void login(JFrame mainFrame) {
        JDialog dialog = new JDialog(mainFrame, "Login", true);
        dialog.setSize(350, 200);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(mainFrame);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(230, 230, 230));

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(0, 102, 204));

        JLabel nameLabel = new JLabel("Enter your username:");
        JTextField nameField = new JTextField(15);

        JLabel passLabel = new JLabel("Enter your password:");
        JPasswordField passField = new JPasswordField(15);

        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");

        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(new Color(255, 255, 255));
        cancelButton.setBackground(new Color(255, 51, 51));
        cancelButton.setForeground(new Color(255, 255, 255));

        loginButton.setFocusPainted(false);
        cancelButton.setFocusPainted(false);

        loginButton.addActionListener(e -> {
            String username = nameField.getText().trim();

            if (!username.isEmpty()) {
                if (validateUser(username)) {
                    int times = 0;
                    while (times < max_login) {
                        String password = String.valueOf(passField.getPassword()); // Move inside the loop
                        if (!password.isEmpty()) {
                            int index = validateUser(username, password);
                            if (index != -1) {
                                showMenu(mainFrame, index);
                                dialog.dispose();
                                return;
                            } else {
                                times++;  //error
                                int resttime = max_login - times;
                                if (resttime > 0) {
                                    JOptionPane.showMessageDialog(dialog, "Invalid password. Attempts left: " + resttime);
                                    passField.setText("");
                                    return;
                                } else {
                                    JOptionPane.showMessageDialog(dialog, "Login failed after " + max_login + " attempts. Please try again later.");
                                    break;
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Enter the password, please.");
                            return;
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, "User does not exist. Please sign up.");
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Invalid username. Please try again.");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(passLabel);
        inputPanel.add(passField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }


    private static void showMenu(JFrame mainFrame, int index) {
        String[] options = {"Know the balance", "Deposit", "Withdraw", "Transfer", "Change password", "EXIT"};
        JPanel panel = new JPanel(new GridLayout(options.length, 1, 0, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JDialog dialog = new JDialog(mainFrame, "ATM - Menu", true); //تمنع التفاعل مع النافذة الرئيسية

        for (String option : options) {
            JButton button = new JButton(option);
            button.setForeground(new Color(255,255,255));
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setPreferredSize(new Dimension(200, 40));

            if (option.equals("EXIT")) {
                button.setBackground(new Color(255, 51, 51));
            } else {
                button.setBackground(new Color(0, 102, 204));
            }

            button.addActionListener(e -> {
                switch (option) {
                    case "Know the balance":
                        int balance = getUserBalance(index);
                        if (balance >= 0) {
                            JOptionPane.showMessageDialog(mainFrame, "Your balance is: $" + balance);
                        } else {
                            JOptionPane.showMessageDialog(mainFrame, "Failed to retrieve balance.");
                        }
                        break;
                    case "Deposit":
                        depositAmount(index);
                        break;
                    case "Withdraw":
                        withdrawAmount(index);
                        break;
                    case "Transfer":
                        transferAmount(index);
                        break;
                    case "Change password":
                        change(mainFrame, getUser(index));
                        break;
                    case "EXIT":
                        JOptionPane.showMessageDialog(mainFrame, "Exiting... Goodbye.");
                        dialog.dispose();
                        break;
                    default:
                        JOptionPane.showMessageDialog(mainFrame, "Invalid choice. Please try again.");
                }
            });
            panel.add(button);
        }

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(panel);
        dialog.pack(); //حساب الأبعاد المناسبة لنافذة الحوار بناءً على حجم محتواها
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }


    private static int getUserBalance(int index) {
        String sql = "SELECT balance FROM userss WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url, db_user, db_password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, index);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("balance");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static User getUser(int index) {
        String sql = "SELECT * FROM userss WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url, db_user, db_password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, index);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    int balance = rs.getInt("balance");
                    return new User(username, password, balance);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void depositAmount(int index) {
        int deposit = 0;
        boolean validInput = false;

        while (!validInput) {
            String input = JOptionPane.showInputDialog("Please enter the amount of deposit (or click Cancel to abort):");

            if (input == null) {
                JOptionPane.showMessageDialog(null, "Deposit operation aborted.");
                return;
            }

            try {
                deposit = Integer.parseInt(input);
                if (deposit <= 0) {
                    throw new IllegalArgumentException("Invalid deposit amount. Please enter a positive value.");
                }
                validInput = true;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
        updateBalance(index, getUserBalance(index) + deposit);
        JOptionPane.showMessageDialog(null, "Deposit value is: " + deposit + "\nNew balance: " + getUserBalance(index));
    }


    private static void withdrawAmount(int index) {
        int withdraw = 0;
        boolean validInput = false;

        while (!validInput) {
            String input = JOptionPane.showInputDialog("Please enter the amount to withdraw (or click Cancel to abort):");

            if (input == null) {
                JOptionPane.showMessageDialog(null, "Withdrawal operation aborted.");
                return;
            }

            try {
                withdraw = Integer.parseInt(input);
                if (withdraw <= 0) {
                    throw new IllegalArgumentException("Invalid withdrawal amount. Please enter a positive value.");
                }
                validInput = true;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }

        int balance = getUserBalance(index);
        if (withdraw > balance) {
            JOptionPane.showMessageDialog(null, "Sorry, you don't have enough balance in your bank account.");
        } else {
            updateBalance(index, balance - withdraw);
            JOptionPane.showMessageDialog(null, "Amount withdrawn: " + withdraw + "\nCurrent balance is: " + getUserBalance(index));
        }
    }


    private static void transferAmount(int index) {
        String name = JOptionPane.showInputDialog("Please enter the name of the account you want to transfer money to (or click Cancel to abort):");
        if (name == null) {
            JOptionPane.showMessageDialog(null, "Transfer operation aborted.");
            return;
        }
        if (name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Invalid username. Please try again.");
            return;
        }
        int index2 = receiver(name);
        if (index2 == -1) {
            JOptionPane.showMessageDialog(null, "This user is not available.");
            return;
        }
        try {
            String amountInput = JOptionPane.showInputDialog("Enter the amount of money you want to transfer (or click Cancel to abort):");
            if (amountInput == null) {
                JOptionPane.showMessageDialog(null, "Transfer operation aborted.");
                return;
            }
            int amount = Integer.parseInt(amountInput);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(null, "Invalid amount entered. Please enter a valid amount.");
                return;
            }
            int balance = getUserBalance(index);
            if (balance < amount) {
                JOptionPane.showMessageDialog(null, "Your bank balance is not enough for the transfer.");
                return;
            }
            updateBalance(index, balance - amount);
            updateBalance(index2, getUserBalance(index2) + amount);
            JOptionPane.showMessageDialog(null, "The balance has been transferred successfully.\nYour balance now is: " + getUserBalance(index));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
        }
    }


    private static void updateBalance(int index, int newBalance) {
        String sql = "UPDATE userss SET balance = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url, db_user, db_password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newBalance);
            pstmt.setInt(2, index);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE userss SET password = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url, db_user, db_password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating password: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    private static int receiver(String username) {
        String sql = "SELECT id FROM userss WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url, db_user, db_password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}