package com.bank.enterprise.ui.panels;

import com.bank.enterprise.ui.BankDesktopApp;
import com.bank.enterprise.ui.api.BankApiClient;
import com.bank.enterprise.ui.components.UIStyles;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private final BankDesktopApp parentApp;
    JPanel card, titlePanel, formPanel;
    GridBagConstraints gbc;
    JLabel titleLabel, usernameLabel, passwordLabel;
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton, clearButton;

    public LoginPanel(BankDesktopApp parentApp) {
        this.parentApp = parentApp;

        setLayout(new GridBagLayout());
        setSize(700, 500);
        setBackground(UIStyles.BACKGROUND_COLOR);

        card = new JPanel(new BorderLayout(0, 20));
        UIStyles.styleCard(card);

        titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        titlePanel.setBackground(UIStyles.BACKGROUND_COLOR);

        ImageIcon logo = new ImageIcon("src/main/java/com/bank/enterprise/ui/Icons/logo.png");
        Image img = logo.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(img);
        JLabel logoLabel = new JLabel(icon);
        logoLabel.setBounds(70, 30, 100, 100);
        titlePanel.add(logoLabel);

        titleLabel = new JLabel("Welcome to Bank Enterprise", SwingConstants.CENTER);
        UIStyles.styleTitle(titleLabel);
        titlePanel.add(titleLabel);

        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIStyles.BACKGROUND_COLOR);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username Label
        usernameLabel = new JLabel("Username");
        UIStyles.styleLabel(usernameLabel);
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        // Username Field
        usernameField = new JTextField(20);
        UIStyles.styleTextField(usernameField);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        // Password Label
        passwordLabel = new JLabel("Password");
        UIStyles.styleLabel(passwordLabel);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        // Password Field
        passwordField = new JPasswordField(20);
        UIStyles.stylePasswordField(passwordField);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        // Clear Button
        clearButton = new JButton("Clear");
        UIStyles.styleButton(clearButton);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(clearButton, gbc);

        // Login Button
        loginButton = new JButton("Login");
        UIStyles.styleButton(loginButton);
        gbc.gridx = 1; gbc.gridwidth = 1;
        formPanel.add(loginButton, gbc);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);

        add(card);

        loginButton.addActionListener(e -> {
            try {
                boolean success = BankApiClient.getInstance().login(usernameField.getText(), new String(passwordField.getPassword()));
                if (success) {
                    parentApp.navigateTo("DASHBOARD");
                } else {
                    JOptionPane.showMessageDialog(LoginPanel.this, "Incorrect username or password");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Connection error.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
        });
    }

}
