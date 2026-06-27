package com.bank.enterprise.ui.panels;

import com.bank.enterprise.ui.BankDesktopApp;
import com.bank.enterprise.ui.api.BankApiClient;
import com.bank.enterprise.ui.components.UIStyles;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

import static com.bank.enterprise.ui.components.UIStyles.*;

public class LoginPanel extends JPanel {

    // ── Public refs (kept for compatibility) ───────────────────────────────────
    JPanel card, titlePanel, formPanel;
    GridBagConstraints gbc;
    JLabel titleLabel, usernameLabel, passwordLabel;
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton, clearButton;

    private JLabel errorLabel;
    private final BankDesktopApp parentApp;

    // ══════════════════════════════════════════════════════════════════════════
    public LoginPanel(BankDesktopApp parentApp) {
        this.parentApp = parentApp;
        setLayout(new GridLayout(1, 2));
        setBackground(BG);

        add(buildBrandPanel());
        add(buildFormPanel());
    }

    // ── Left: Branded accent panel ─────────────────────────────────────────────
    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Subtle diagonal grid watermark
                g2.setColor(new Color(0xFF, 0xFF, 0xFF, 8));
                g2.setStroke(new BasicStroke(1f));
                for (int i = -getHeight(); i < getWidth() + getHeight(); i += 40) {
                    g2.drawLine(i, 0, i + getHeight(), getHeight());
                }

                // Large monogram watermark
                g2.setFont(FONT_MONOGRAM);
                g2.setColor(new Color(0xC9, 0xA8, 0x4C, 20));
                FontMetrics fm = g2.getFontMetrics();
                String mono = "EB";
                int mx = (getWidth() - fm.stringWidth(mono)) / 2;
                int my = (getHeight() + fm.getAscent()) / 2;
                g2.drawString(mono, mx, my);

                g2.dispose();
            }
        };
        panel.setBackground(SURFACE);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(new Color(0, 0, 0, 0));
        inner.setOpaque(false);

        // Logo
        ImageIcon logo = new ImageIcon("src/main/java/com/bank/enterprise/ui/Icons/logo.png");
        Image img = logo.getImage().getScaledInstance(72, 72, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(img));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Brand name
        JLabel brand = new JLabel("ENTERPRISE BANK");
        brand.setFont(FONT_BRAND);
        brand.setForeground(ACCENT_GOLD);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(DIVIDER);
        sep.setMaximumSize(new Dimension(120, 1));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tagline
        JLabel tagline = new JLabel("<html><center>Secure. Personal.<br>Trusted.</center></html>");
        tagline.setFont(new Font("Segoe UI", Font.BOLD, 22));
        tagline.setForeground(TEXT_PRIMARY);
        tagline.setHorizontalAlignment(SwingConstants.CENTER);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Banking built around you");
        sub.setFont(FONT_SUBHEAD);
        sub.setForeground(TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Account number decorative strip (flavour)
        JLabel accentStrip = new JLabel("·· ···· ···· 2024");
        accentStrip.setFont(FONT_MONO);
        accentStrip.setForeground(new Color(ACCENT_GOLD.getRed(), ACCENT_GOLD.getGreen(), ACCENT_GOLD.getBlue(), 100));
        accentStrip.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(logoLabel);
        inner.add(brand);
        inner.add(Box.createVerticalStrut(16));
        inner.add(sep);
        inner.add(Box.createVerticalStrut(20));
        inner.add(tagline);
        inner.add(Box.createVerticalStrut(10));
        inner.add(sub);
        inner.add(Box.createVerticalStrut(32));
        inner.add(accentStrip);

        panel.add(inner);
        return panel;
    }

    // ── Right: Login form panel ────────────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG);

        card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(DIVIDER, 1, 14),
                BorderFactory.createEmptyBorder(36, 40, 36, 40)
        ));

        // Title block
        titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(CARD_BG);
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleLabel = new JLabel("Sign in");
        titleLabel.setFont(FONT_HEADLINE);
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Enter your credentials to continue");
        subtitle.setFont(FONT_SUBHEAD);
        subtitle.setForeground(TEXT_MUTED);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitle);

        // Form fields
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_BG);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameLabel = makeFieldLabel("Username");
        usernameField = new JTextField(20);
        styleField(usernameField, "e.g. john.doe");

        passwordLabel = makeFieldLabel("Password");
        passwordField = new JPasswordField(20);
        styleField(passwordField, "••••••••");

        // Error label (hidden until needed)
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(usernameLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(18));
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(errorLabel);

        // Buttons
        loginButton = makeSolidButton("Sign in", ACCENT_GOLD, Color.BLACK);
        clearButton = makeGhostButton("Clear fields", ACCENT_BLUE);

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setBackground(CARD_BG);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.add(loginButton);
        btnRow.add(clearButton);

        // Assemble card
        card.add(titlePanel);
        card.add(Box.createVerticalStrut(28));
        card.add(formPanel);
        card.add(Box.createVerticalStrut(24));
        card.add(btnRow);

        // Constrain card width
        card.setMaximumSize(new Dimension(380, Integer.MAX_VALUE));
        card.setPreferredSize(new Dimension(360, 380));

        wrapper.add(card);

        // ── Wire actions ───────────────────────────────────────────────────────
        loginButton.addActionListener(e -> attemptLogin());

        // Allow Enter key from either field
        ActionListener enterLogin = e -> attemptLogin();
        usernameField.addActionListener(enterLogin);
        passwordField.addActionListener(enterLogin);

        clearButton.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
            errorLabel.setText(" ");
            usernameField.requestFocusInWindow();
        });

        return wrapper;
    }

    // ── Login logic ────────────────────────────────────────────────────────────
    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            showError("Please enter your username.");
            usernameField.requestFocusInWindow();
            return;
        }
        if (password.isEmpty()) {
            showError("Please enter your password.");
            passwordField.requestFocusInWindow();
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Signing in…");
        errorLabel.setText(" ");

        SwingUtilities.invokeLater(() -> {
            try {
                boolean success = BankApiClient.getInstance().login(username, password);
                if (success) {
                    parentApp.navigateTo("DASHBOARD");
                } else {
                    showError("Username or password is incorrect.");
                    passwordField.setText("");
                    passwordField.requestFocusInWindow();
                }
            } catch (Exception ex) {
                showError("Connection failed — please check your network.");
            } finally {
                loginButton.setEnabled(true);
                loginButton.setText("Sign in");
            }
        });
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        // Shake the card slightly for tactile feedback
        shakeComponent(card);
    }

    private void shakeComponent(JComponent c) {
        Point origin = c.getLocation();
        Timer shake = new Timer(30, null);
        int[] offsets = {-6, 6, -4, 4, -2, 2, 0};
        int[] step = {0};
        shake.addActionListener(e -> {
            if (step[0] >= offsets.length) {
                ((Timer) e.getSource()).stop();
                c.setLocation(origin);
                return;
            }
            c.setLocation(origin.x + offsets[step[0]], origin.y);
            step[0]++;
        });
        shake.start();
    }

    // ── Field / button factories ───────────────────────────────────────────────
    private JLabel makeFieldLabel(String text) {
        JLabel lbl = new JLabel(text.toUpperCase());
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void styleField(JTextField field, String placeholder) {
        field.setFont(FONT_INPUT);
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT_GOLD);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(FIELD_BORDER, 1, 8),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Placeholder hint
        field.setToolTipText(placeholder);

        // Focus ring: gold border on focus
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedLineBorder(ACCENT_GOLD, 1, 8),
                        BorderFactory.createEmptyBorder(10, 14, 10, 14)
                ));
            }
            @Override public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedLineBorder(FIELD_BORDER, 1, 8),
                        BorderFactory.createEmptyBorder(10, 14, 10, 14)
                ));
            }
        });
    }

    private JButton makeSolidButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled()
                        ? (getModel().isRollover() ? bg.darker() : bg)
                        : bg.darker().darker());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        return btn;
    }

    private JButton makeGhostButton(String text, Color accent) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(accent);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(accent, 1, 8),
                BorderFactory.createEmptyBorder(9, 20, 9, 20)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        return btn;
    }

    // ── Inner: Rounded line border (shared with Dashboard) ────────────────────
    static class RoundedLineBorder extends AbstractBorder {
        private final Color color;
        private final int thickness, radius;

        RoundedLineBorder(Color c, int t, int r) {
            color = c; thickness = t; radius = r;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Double(x + 0.5, y + 0.5, w - 1, h - 1, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
    }
}