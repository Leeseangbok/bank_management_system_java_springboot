package com.bank.enterprise.ui;


import com.bank.enterprise.entity.Transaction;
import com.bank.enterprise.ui.panels.DashboardPanel;
import com.bank.enterprise.ui.panels.LoginPanel;
import com.bank.enterprise.ui.panels.OpenAccountPanel;
import com.bank.enterprise.ui.panels.TransactionPanel;

import javax.swing.*;
import java.awt.*;

public class BankDesktopApp extends JFrame {

    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    private final DashboardPanel dashboardPanel;

    public BankDesktopApp() {
        setTitle("Enterprise Bank - Personal Online Banking");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        LoginPanel loginPanel = new LoginPanel(this);
        OpenAccountPanel openAccountPanel = new OpenAccountPanel(this);
        TransactionPanel transactionPanel = new TransactionPanel(this);
        dashboardPanel = new DashboardPanel(this);

        cardPanel.add(loginPanel, "LOGIN");
        cardPanel.add(dashboardPanel, "DASHBOARD");
        cardPanel.add(transactionPanel, "TRANSACTION");
        cardPanel.add(openAccountPanel, "OPEN ACCOUNT");

        add(cardPanel);
    }

    public void navigateTo(String cardName) {
        cardLayout.show(cardPanel, cardName);

        if (cardName.equals("DASHBOARD")) {
            dashboardPanel.refreshData();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            BankDesktopApp app = new BankDesktopApp();
            app.setVisible(true);
        });
    }
}
