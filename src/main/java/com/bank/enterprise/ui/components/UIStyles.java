package com.bank.enterprise.ui.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class UIStyles {

    // =========================
    // Colors
    // =========================
    public static final Color PRIMARY_COLOR = new Color(0, 102, 204);
    public static final Color PRIMARY_DARK = new Color(0, 76, 153);

    public static final Color BACKGROUND_COLOR = new Color(240, 248, 255);
    public static final Color CARD_BACKGROUND = Color.WHITE;

    public static final Color TEXT_COLOR = new Color(50, 50, 50);
    public static final Color TEXT_LIGHT = new Color(120, 120, 120);

    public static final Color SUCCESS_COLOR = new Color(46, 125, 50);
    public static final Color WARNING_COLOR = new Color(255, 143, 0);
    public static final Color ERROR_COLOR = new Color(211, 47, 47);

    public static final Color BORDER_COLOR = new Color(220, 220, 220);

    // =========================
    // Fonts
    // =========================
    public static final Font TITLE_FONT =
            new Font("Segoe UI", Font.BOLD, 28);

    public static final Font SUBTITLE_FONT =
            new Font("Segoe UI", Font.BOLD, 18);

    public static final Font LABEL_FONT =
            new Font("Segoe UI", Font.BOLD, 14);

    public static final Font NORMAL_FONT =
            new Font("Segoe UI", Font.PLAIN, 14);

    public static final Font BUTTON_FONT =
            new Font("Segoe UI", Font.BOLD, 14);

    public static final Font TABLE_HEADER_FONT =
            new Font("Segoe UI", Font.BOLD, 14);

    public static final Font TABLE_CONTENT_FONT =
            new Font("Segoe UI", Font.PLAIN, 13);

    // =========================
    // Dimensions
    // =========================
    public static final Dimension TEXT_FIELD_SIZE =
            new Dimension(250, 35);

    public static final Dimension BUTTON_SIZE =
            new Dimension(120, 40);

    public static final Dimension LARGE_BUTTON_SIZE =
            new Dimension(180, 45);

    // =========================
    // Padding & Borders
    // =========================
    public static final Border PANEL_PADDING =
            new EmptyBorder(20, 20, 20, 20);

    public static final Border CARD_BORDER =
            BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_COLOR, 1, true),
                    new EmptyBorder(25, 25, 25, 25)
            );

    public static final Border FIELD_BORDER =
            BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_COLOR, 1, true),
                    new EmptyBorder(5, 10, 5, 10)
            );

    // =========================
    // Component Styling Methods
    // =========================
    public static void styleButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setPreferredSize(BUTTON_SIZE);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleTextField(JTextField field) {
        field.setFont(NORMAL_FONT);
        field.setPreferredSize(TEXT_FIELD_SIZE);
        field.setBorder(FIELD_BORDER);
    }

    public static void stylePasswordField(JPasswordField field) {
        field.setFont(NORMAL_FONT);
        field.setPreferredSize(TEXT_FIELD_SIZE);
        field.setBorder(FIELD_BORDER);
    }

    public static void styleLabel(JLabel label) {
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
    }

    public static void styleTitle(JLabel label) {
        label.setFont(TITLE_FONT);
        label.setForeground(PRIMARY_COLOR);
    }

    public static void styleCard(JPanel panel) {
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(CARD_BORDER);
    }
}