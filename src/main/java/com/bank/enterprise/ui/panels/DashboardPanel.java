package com.bank.enterprise.ui.panels;

import com.bank.enterprise.ui.BankDesktopApp;
import com.bank.enterprise.ui.api.BankApiClient;
import com.bank.enterprise.ui.components.UIStyles;
import com.fasterxml.jackson.databind.JsonNode;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private final BankDesktopApp parentApp;
    private final JTextArea accountsDisplay;
    private final JLabel welcomeLabel;
    JPanel topPanel, buttonPanel;
    JScrollPane scrollPane;
    JButton openAccountButton, refreshButton, transferButton, logoutButton;


    public DashboardPanel(BankDesktopApp parentApp) {
        this.parentApp = parentApp;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIStyles.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top Section: Welcome Message
        topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIStyles.BACKGROUND_COLOR);

        welcomeLabel = new JLabel("My Accounts Dashboard");
        welcomeLabel.setFont(UIStyles.TITLE_FONT);
        welcomeLabel.setForeground(UIStyles.PRIMARY_COLOR);
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // Center Section: Accounts Display
        accountsDisplay = new JTextArea("Click 'Refresh' to load your accounts...");
        accountsDisplay.setEditable(false);
        accountsDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14));
        accountsDisplay.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        scrollPane = new JScrollPane(accountsDisplay);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Section: Customer Actions
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(UIStyles.BACKGROUND_COLOR);

        openAccountButton = new JButton("Open Account");
        refreshButton = new JButton("Refresh");
        transferButton = new JButton("Transfer");
        logoutButton = new JButton("Logout");

        transferButton.setBackground(new Color(34, 139, 34));
        logoutButton.setBackground(Color.DARK_GRAY);

        buttonPanel.add(openAccountButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(transferButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button Actions
        openAccountButton.addActionListener(e -> {
            parentApp.navigateTo("OPEN ACCOUNT");
        });

        refreshButton.addActionListener(e -> refreshData());

        logoutButton.addActionListener(e -> {
            BankApiClient.getInstance().logout();
            accountsDisplay.setText(""); // Clear sensitive data
            parentApp.navigateTo("LOGIN");
        });

        transferButton.addActionListener(e -> {
            parentApp.navigateTo("TRANSACTION"); // We will build this next!
        });
    }

    public void refreshData() {
        try {
            JsonNode accounts = BankApiClient.getInstance().getMyAccounts();
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-20s | %-15s | %-15s\n", "Account Number", "Account Type", "Available Balance"));
            sb.append("------------------------------------------------------------------\n");

            String ownerName = "";
            for (JsonNode acc : accounts) {
                if (ownerName.isEmpty()) {
                    ownerName = acc.path("ownerName").asText();
                }
                String accNum = acc.path("accountNumber").asText();
                String type = acc.path("accountType").asText("SAVINGS");
                double balance = acc.path("balance").asDouble();
                sb.append(String.format("%-20s | %-15s | $%-14.2f\n", accNum, type, balance));
            }

            if (!ownerName.isEmpty()) {
                welcomeLabel.setText("Welcome back, " + ownerName);
            }

            if (accounts.isEmpty()) {
                sb.append("\nYou do not have any open accounts yet.");
            }

            accountsDisplay.setText(sb.toString());
        } catch (Exception ex) {
            accountsDisplay.setText("Error loading your accounts. Please check your connection.");
        }
    }
}