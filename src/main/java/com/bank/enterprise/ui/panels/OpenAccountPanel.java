package com.bank.enterprise.ui.panels;

import com.bank.enterprise.ui.BankDesktopApp;
import com.bank.enterprise.ui.api.BankApiClient;
import com.bank.enterprise.ui.components.UIStyles;

import javax.swing.*;
import java.awt.*;

public class OpenAccountPanel extends JPanel {

    private final BankDesktopApp parentApp;

    public OpenAccountPanel(BankDesktopApp parentApp) {
        this.parentApp = parentApp;
        setLayout(new GridBagLayout());
        setBackground(UIStyles.BACKGROUND_COLOR);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Open a New Account", SwingConstants.CENTER);
        titleLabel.setFont(UIStyles.TITLE_FONT);
        titleLabel.setForeground(UIStyles.PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(titleLabel, gbc);

        // Account Type Dropdown
        JLabel typeLabel = new JLabel("Select Account Type:");
        typeLabel.setFont(UIStyles.LABEL_FONT);
        gbc.gridy = 1; gbc.gridwidth = 1;
        card.add(typeLabel, gbc);

        String[] types = {"SAVINGS", "CHECKING"};
        JComboBox<String> typeDropdown = new JComboBox<>(types);
        typeDropdown.setFont(UIStyles.NORMAL_FONT);
        typeDropdown.setBackground(Color.WHITE);
        gbc.gridx = 1;
        card.add(typeDropdown, gbc);

        // Submit Button
        JButton submitButton = new JButton("Open Account");
        submitButton.setBackground(new Color(34, 139, 34)); // Green
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(UIStyles.LABEL_FONT);
        submitButton.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        card.add(submitButton, gbc);

        // Back Button
        JButton backButton = new JButton("Cancel");
        backButton.setBackground(Color.DARK_GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.setFont(UIStyles.LABEL_FONT);
        backButton.setFocusPainted(false);
        gbc.gridy = 3;
        card.add(backButton, gbc);

        add(card); // Add the white card to the main Alice Blue background

        // Button Actions
        backButton.addActionListener(e -> parentApp.navigateTo("DASHBOARD"));

        submitButton.addActionListener(e -> {
            try {
                String selectedType = (String) typeDropdown.getSelectedItem();
                boolean success = BankApiClient.getInstance().openNewAccount(selectedType);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Account successfully opened!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    parentApp.navigateTo("DASHBOARD"); // Go back and auto-refresh
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to open account. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Network error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}