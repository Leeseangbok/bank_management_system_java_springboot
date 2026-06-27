package com.bank.enterprise.ui.panels;

import com.bank.enterprise.ui.BankDesktopApp;
import com.bank.enterprise.ui.api.BankApiClient;
import com.bank.enterprise.ui.components.UIStyles;
import com.fasterxml.jackson.databind.JsonNode;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private final BankDesktopApp parentApp;
    private final JTextArea accountDisplay;

    public DashboardPanel(BankDesktopApp parentApp) {
        this.parentApp = parentApp;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel titleLabel = new JLabel("Customer Accounts Dashboard");
        titleLabel.setFont(UIStyles.TITLE_FONT);
        add(titleLabel, BorderLayout.NORTH);

        accountDisplay = new JTextArea("Click 'Refresh' to load accounts...");
        accountDisplay.setEditable(false);
        accountDisplay.setFont(new Font("monospaced", Font.BOLD, 14));
        add(new JScrollPane(accountDisplay), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        JButton refreshButton = new JButton("Refresh");
        JButton logoutButton = new JButton("Logout");

        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> refreshData());

        logoutButton.addActionListener(e -> {
            BankApiClient.getInstance().logout();
            parentApp.navigateTO("LOGIN");
        });
    }

    public void refreshData() {
        try {
            JsonNode accounts = BankApiClient.getInstance().getMyAccounts();
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-20s | %-15s | %-15s\n", "Account Number", "Type", "Balance ($)"));
            sb.append("----------------------------------------------------------------\n");

            for (JsonNode account: accounts) {
                String accountNumber = account.get("accountNumber").asText();
                String accountType = account.get("accountType").asText();
                double balance = account.get("balance").asDouble();
                sb.append(String.format("%-20s | %-15s | %-15s\n", accountNumber, accountType, balance));
            }
            accountDisplay.setText(sb.toString());
        } catch (Exception e) {
            accountDisplay.setText("Error loading accounts!");
        }
    }

}
