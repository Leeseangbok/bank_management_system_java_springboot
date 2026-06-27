package com.bank.enterprise.ui.panels;

import com.bank.enterprise.ui.BankDesktopApp;
import com.bank.enterprise.ui.components.UIStyles;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private final BankDesktopApp parentApp;

    public LoginPanel(BankDesktopApp parentApp) {
        this.parentApp = parentApp;
        setLayout(new GridLayout());
        setBackground(UIStyles.BACKGROUND_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Welcome to Bank Enterprise");
        titleLabel.setFont(UIStyles.TITLE_FONT);
        titleLabel.setForeground(UIStyles.PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);
    }

}
