package com.bank;

import com.bank.dao.AccountDAO;
import com.bank.dao.AccountDAOImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;

public class BankAppGUI extends JFrame {

    private final AccountDAO dao = new AccountDAOImpl();

    // UI Components
    private JTextField txtAccNo, txtCustId, txtAmount, txtTargetAcc;
    private JComboBox<String> cmbAccType;
    private JLabel lblBalanceDisplay;
    private JTable tblStatement;
    private DefaultTableModel tableModel;

    public BankAppGUI() {
        setTitle("Banking Management System");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Header Banner
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(24, 44, 97));
        JLabel title = new JLabel("BANK MANAGEMENT PORTAL");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        headerPanel.add(title);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Tabbed Form Layout (similar to management systems)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Account Operations", createOperationsPanel());
        tabbedPane.addTab("Open Account", createNewAccountPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // 3. Mini-Statement Table at Bottom
        JPanel statementPanel = createStatementPanel();
        add(statementPanel, BorderLayout.SOUTH);
    }

    private JPanel createOperationsPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        panel.add(new JLabel("Account Number:"));
        txtAccNo = new JTextField();
        panel.add(txtAccNo);

        panel.add(new JLabel("Amount ($):"));
        txtAmount = new JTextField();
        panel.add(txtAmount);

        panel.add(new JLabel("Target Account (for Transfer):"));
        txtTargetAcc = new JTextField();
        panel.add(txtTargetAcc);

        // Buttons
        JButton btnCheckBal = new JButton("Check Balance");
        JButton btnDeposit = new JButton("Deposit");
        JButton btnWithdraw = new JButton("Withdraw");
        JButton btnTransfer = new JButton("Transfer Funds");

        panel.add(btnCheckBal);
        panel.add(btnDeposit);
        panel.add(btnWithdraw);
        panel.add(btnTransfer);

        lblBalanceDisplay = new JLabel("Balance: --", SwingConstants.CENTER);
        lblBalanceDisplay.setFont(new Font("Arial", Font.BOLD, 15));
        lblBalanceDisplay.setForeground(new Color(39, 174, 96));
        panel.add(new JLabel("Current Status:"));
        panel.add(lblBalanceDisplay);

        // Event Listeners
        btnCheckBal.addActionListener(e -> checkBalance());
        btnDeposit.addActionListener(e -> handleDeposit());
        btnWithdraw.addActionListener(e -> handleWithdraw());
        btnTransfer.addActionListener(e -> handleTransfer());

        return panel;
    }

    private JPanel createNewAccountPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtNewAccNo = new JTextField();
        JTextField txtNewCustId = new JTextField();
        JComboBox<String> cmbType = new JComboBox<>(new String[]{"SAVINGS", "CURRENT"});
        JTextField txtInitDeposit = new JTextField();

        panel.add(new JLabel("New Account Number:"));
        panel.add(txtNewAccNo);

        panel.add(new JLabel("Customer ID (e.g., 1 or 2):"));
        panel.add(txtNewCustId);

        panel.add(new JLabel("Account Type:"));
        panel.add(cmbType);

        panel.add(new JLabel("Initial Deposit ($):"));
        panel.add(txtInitDeposit);

        JButton btnCreate = new JButton("Register Account");
        btnCreate.setBackground(new Color(41, 128, 185));
        btnCreate.setForeground(Color.WHITE);
        panel.add(new JLabel(""));
        panel.add(btnCreate);

        btnCreate.addActionListener(e -> {
            try {
                long accNo = Long.parseLong(txtNewAccNo.getText().trim());
                int custId = Integer.parseInt(txtNewCustId.getText().trim());
                String type = (String) cmbType.getSelectedItem();
                BigDecimal dep = new BigDecimal(txtInitDeposit.getText().trim());

                dao.createAccount(accNo, custId, type, dep);
                JOptionPane.showMessageDialog(this, "Account created successfully!");
                txtNewAccNo.setText("");
                txtNewCustId.setText("");
                txtInitDeposit.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Input Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createStatementPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Recent Transactions"));
        panel.setPreferredSize(new Dimension(750, 220));

        tableModel = new DefaultTableModel(new String[]{"Txn ID", "Type", "Amount", "Target Acc", "Timestamp"}, 0);
        tblStatement = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblStatement);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // --- Action Handlers ---
    private void checkBalance() {
        try {
            long acc = Long.parseLong(txtAccNo.getText().trim());
            BigDecimal bal = dao.getBalance(acc);
            if (bal != null) {
                lblBalanceDisplay.setText("Balance: $" + bal);
            } else {
                JOptionPane.showMessageDialog(this, "Account not found!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Account Number.");
        }
    }

    private void handleDeposit() {
        try {
            long acc = Long.parseLong(txtAccNo.getText().trim());
            BigDecimal amount = new BigDecimal(txtAmount.getText().trim());
            dao.deposit(acc, amount);
            checkBalance();
            JOptionPane.showMessageDialog(this, "Deposit Successful!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Deposit Failed: " + ex.getMessage());
        }
    }

    private void handleWithdraw() {
        try {
            long acc = Long.parseLong(txtAccNo.getText().trim());
            BigDecimal amount = new BigDecimal(txtAmount.getText().trim());
            dao.withdraw(acc, amount);
            checkBalance();
            JOptionPane.showMessageDialog(this, "Withdrawal Successful!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Withdrawal Failed: " + ex.getMessage());
        }
    }

    private void handleTransfer() {
        try {
            long from = Long.parseLong(txtAccNo.getText().trim());
            long to = Long.parseLong(txtTargetAcc.getText().trim());
            BigDecimal amount = new BigDecimal(txtAmount.getText().trim());
            dao.transfer(from, to, amount);
            checkBalance();
            JOptionPane.showMessageDialog(this, "Transfer of $" + amount + " to " + to + " Successful!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Transfer Failed: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BankAppGUI().setVisible(true);
        });
    }
}