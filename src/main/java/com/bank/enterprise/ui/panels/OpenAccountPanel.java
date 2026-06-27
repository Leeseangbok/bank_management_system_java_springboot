package com.bank.enterprise.ui.panels;

import com.bank.enterprise.ui.BankDesktopApp;
import com.bank.enterprise.ui.api.BankApiClient;
import com.bank.enterprise.ui.components.UIStyles;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class OpenAccountPanel extends JPanel {

    public static class KeyValue {
        @Getter
        private final int id;
        private final String name;

        public KeyValue(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public OpenAccountPanel(BankDesktopApp parentApp) {
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
        Map<Integer, String> types = Map.of(
                1, "SAVING",
                2, "CURRENT",
                3, "FIXED DEPOSIT",
                4, "BUSINESS",
                5, "LOAN"
        );

        JComboBox<KeyValue> typeDropdown = new JComboBox<>();

        types.forEach((id, name) -> typeDropdown.addItem(new KeyValue(id, name)));

        typeDropdown.setFont(UIStyles.NORMAL_FONT);
        typeDropdown.setBackground(Color.WHITE);
        gbc.gridx = 1;
        card.add(typeDropdown, gbc);

        // Submit Button
        JButton submitButton = new JButton("Open Account");
        submitButton.setBackground(new Color(34, 139, 34)); // Green
        submitButton.setFont(UIStyles.LABEL_FONT);
        submitButton.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        card.add(submitButton, gbc);

        // Back Button
        JButton backButton = new JButton("Cancel");
        backButton.setBackground(Color.DARK_GRAY);
        backButton.setFont(UIStyles.LABEL_FONT);
        backButton.setFocusPainted(false);
        gbc.gridy = 3;
        card.add(backButton, gbc);

        add(card);

        // Button Actions
        backButton.addActionListener(e -> parentApp.navigateTo("DASHBOARD"));

        submitButton.addActionListener(e -> {
            try {
                KeyValue selected = (KeyValue) typeDropdown.getSelectedItem();

                assert selected != null;
                int accountTypeId = selected.getId();

                boolean success = BankApiClient.getInstance().openNewAccount(accountTypeId);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Account successfully opened!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    parentApp.navigateTo("DASHBOARD");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to open account. Please try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Network error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}