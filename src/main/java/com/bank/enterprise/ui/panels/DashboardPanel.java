package com.bank.enterprise.ui.panels;

import com.bank.enterprise.ui.BankDesktopApp;
import com.bank.enterprise.ui.api.BankApiClient;
import com.bank.enterprise.ui.components.UIStyles;
import com.fasterxml.jackson.databind.JsonNode;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

import static com.bank.enterprise.ui.components.UIStyles.*;

public class DashboardPanel extends JPanel {

    private final BankDesktopApp parentApp;
    private JLabel welcomeLabel;
    private JLabel subtitleLabel;
    private JPanel cardsContainer;
    private JLabel statusLabel;

    // ── Public refs (kept for compatibility) ──────────────────────────────────
    JPanel topPanel, buttonPanel;
    JScrollPane scrollPane;
    JButton openAccountButton, refreshButton, transferButton, logoutButton;

    // ══════════════════════════════════════════════════════════════════════════
    public DashboardPanel(BankDesktopApp parentApp) {
        this.parentApp = parentApp;
        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
    }

    // ── Header ─────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        topPanel = new JPanel(new BorderLayout(0, 4));
        topPanel.setBackground(SURFACE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                new BottomBorder(DIVIDER, 1),
                BorderFactory.createEmptyBorder(18, 28, 18, 28)
        ));

        // Brand mark
        JLabel brand = new JLabel("● ENTERPRISE BANK");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 11));
        brand.setForeground(ACCENT_GOLD);

        // Welcome
        welcomeLabel = new JLabel("My Accounts Dashboard");
        welcomeLabel.setFont(FONT_HEADLINE);
        welcomeLabel.setForeground(TEXT_PRIMARY);

        subtitleLabel = new JLabel("Click Refresh to load your accounts");
        subtitleLabel.setFont(FONT_SUBHEAD);
        subtitleLabel.setForeground(TEXT_MUTED);

        JPanel textStack = new JPanel();
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        textStack.setBackground(SURFACE);
        textStack.add(brand);
        textStack.add(Box.createVerticalStrut(6));
        textStack.add(welcomeLabel);
        textStack.add(Box.createVerticalStrut(2));
        textStack.add(subtitleLabel);

        topPanel.add(textStack, BorderLayout.WEST);
        return topPanel;
    }

    // ── Center ─────────────────────────────────────────────────────────────────
    private JScrollPane buildCenter() {
        cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBackground(BG);
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Placeholder state
        cardsContainer.add(buildPlaceholderCard());

        scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(BG);
        scrollPane.getViewport().setBackground(BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    // ── Footer ─────────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(SURFACE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                new TopBorder(DIVIDER, 1),
                BorderFactory.createEmptyBorder(14, 28, 14, 28)
        ));

        // Left: status
        statusLabel = new JLabel(" ");
        statusLabel.setFont(FONT_SUBHEAD);
        statusLabel.setForeground(TEXT_MUTED);

        // Right: action buttons
        openAccountButton = makeGhostButton("+ Open Account", ACCENT_GOLD);
        refreshButton     = makeGhostButton("↻  Refresh",      ACCENT_BLUE);
        transferButton    = makeSolidButton("→  Transfer",      POSITIVE);
        logoutButton      = makeSolidButton("Logout",           DANGER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setBackground(SURFACE);
        actions.add(openAccountButton);
        actions.add(refreshButton);
        actions.add(makeSeparator());
        actions.add(transferButton);
        actions.add(logoutButton);

        buttonPanel.add(statusLabel, BorderLayout.WEST);
        buttonPanel.add(actions,     BorderLayout.EAST);

        // Wire actions
        openAccountButton.addActionListener(e -> parentApp.navigateTo("OPEN ACCOUNT"));
        refreshButton.addActionListener(e -> refreshData());
        transferButton.addActionListener(e -> parentApp.navigateTo("TRANSACTION"));
        logoutButton.addActionListener(e -> {
            BankApiClient.getInstance().logout();
            cardsContainer.removeAll();
            cardsContainer.add(buildPlaceholderCard());
            cardsContainer.revalidate();
            cardsContainer.repaint();
            welcomeLabel.setText("My Accounts Dashboard");
            subtitleLabel.setText("Click Refresh to load your accounts");
            parentApp.navigateTo("LOGIN");
        });

        return buttonPanel;
    }

    // ── Data refresh ───────────────────────────────────────────────────────────
    public void refreshData() {
        statusLabel.setText("Loading…");
        refreshButton.setEnabled(false);

        SwingUtilities.invokeLater(() -> {
            try {
                JsonNode accounts = BankApiClient.getInstance().getMyAccounts();
                cardsContainer.removeAll();

                String ownerName = "";
                int count = 0;

                for (JsonNode acc : accounts) {
                    if (ownerName.isEmpty()) ownerName = acc.path("ownerName").asText();
                    String accNum  = acc.path("accountNumber").asText();
                    String type    = acc.path("accountType").asText("SAVINGS");
                    double balance = acc.path("balance").asDouble();

                    cardsContainer.add(buildAccountCard(accNum, type, balance, count));
                    cardsContainer.add(Box.createVerticalStrut(14));
                    count++;
                }

                if (count == 0) {
                    cardsContainer.add(buildEmptyCard());
                }

                if (!ownerName.isEmpty()) {
                    welcomeLabel.setText("Welcome back, " + ownerName);
                    subtitleLabel.setText(count + " account" + (count == 1 ? "" : "s") + " on file");
                }

                statusLabel.setText("Last updated just now");

            } catch (Exception ex) {
                cardsContainer.removeAll();
                cardsContainer.add(buildErrorCard());
                statusLabel.setText("Could not connect — check your network");
            }

            refreshButton.setEnabled(true);
            cardsContainer.revalidate();
            cardsContainer.repaint();
        });
    }

    // ── Card builders ──────────────────────────────────────────────────────────
    private JPanel buildAccountCard(String accNum, String type, double balance, int index) {
        Color stripe = (type.equalsIgnoreCase("CHECKING") || type.equalsIgnoreCase("CURRENT"))
                ? ACCENT_BLUE : ACCENT_GOLD;

        RoundedPanel card = new RoundedPanel(10, CARD_BG);
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setPreferredSize(new Dimension(500, 110));

        // Left stripe
        JPanel stripeBar = new JPanel();
        stripeBar.setBackground(stripe);
        stripeBar.setPreferredSize(new Dimension(5, 0));
        card.add(stripeBar, BorderLayout.WEST);

        // Content
        JPanel content = new JPanel(new BorderLayout(0, 6));
        content.setBackground(CARD_BG);
        content.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Type pill + number
        JLabel typeLabel = new JLabel(type.toUpperCase());
        typeLabel.setFont(FONT_LABEL);
        typeLabel.setForeground(stripe);

        JLabel numLabel = new JLabel(formatAccountNumber(accNum));
        numLabel.setFont(FONT_MONO);
        numLabel.setForeground(TEXT_MUTED);

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        topRow.setBackground(CARD_BG);
        topRow.add(typeLabel);
        topRow.add(numLabel);

        // Balance
        JLabel balLabel = new JLabel(String.format("$%,.2f", balance));
        balLabel.setFont(FONT_BALANCE);
        balLabel.setForeground(balance >= 0 ? TEXT_PRIMARY : DANGER);

        JLabel balCaption = new JLabel("Available balance");
        balCaption.setFont(FONT_SUBHEAD);
        balCaption.setForeground(TEXT_MUTED);

        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bottomRow.setBackground(CARD_BG);
        bottomRow.add(balLabel);
        bottomRow.add(balCaption);

        content.add(topRow,    BorderLayout.NORTH);
        content.add(bottomRow, BorderLayout.SOUTH);

        card.add(content, BorderLayout.CENTER);

        // Hover highlight
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(0x213348));
                content.setBackground(new Color(0x213348));
                topRow.setBackground(new Color(0x213348));
                bottomRow.setBackground(new Color(0x213348));
                card.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BG);
                content.setBackground(CARD_BG);
                topRow.setBackground(CARD_BG);
                bottomRow.setBackground(CARD_BG);
                card.repaint();
            }
        });

        return card;
    }

    private JPanel buildPlaceholderCard() {
        return buildMessageCard("↻", "No accounts loaded yet", "Click Refresh below to fetch your accounts", TEXT_MUTED);
    }

    private JPanel buildEmptyCard() {
        return buildMessageCard("+", "No accounts open", "Open your first account using the button below", ACCENT_GOLD);
    }

    private JPanel buildErrorCard() {
        return buildMessageCard("✕", "Couldn't load accounts", "Please check your connection and try again", DANGER);
    }

    private JPanel buildMessageCard(String icon, String title, String sub, Color accent) {
        RoundedPanel card = new RoundedPanel(10, CARD_BG);
        card.setLayout(new GridBagLayout());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(CARD_BG);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        iconLabel.setForeground(accent);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel(sub);
        subLabel.setFont(FONT_SUBHEAD);
        subLabel.setForeground(TEXT_MUTED);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(iconLabel);
        inner.add(Box.createVerticalStrut(8));
        inner.add(titleLabel);
        inner.add(Box.createVerticalStrut(4));
        inner.add(subLabel);

        card.add(inner);
        return card;
    }

    // ── Button factories ───────────────────────────────────────────────────────
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
        styleButton(btn);
        btn.setForeground(accent);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(accent, 1, 8),
                BorderFactory.createEmptyBorder(7, 16, 7, 16)
        ));
        return btn;
    }

    private JButton makeSolidButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = getModel().isRollover() ? bg.darker() : bg;
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleButton(btn);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return btn;
    }

    private void styleButton(JButton btn) {
        btn.setFont(FONT_BTN);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
    }

    private JSeparator makeSeparator() {
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 24));
        sep.setForeground(DIVIDER);
        return sep;
    }

    // ── Helpers ────────────────────────────────────────────────────────────────
    private String formatAccountNumber(String raw) {
        // Insert spaces every 4 chars for readability: 1234 5678 9012
        return raw.replaceAll("(.{4})(?!$)", "$1 ");
    }

    // ── Inner: Rounded panel ───────────────────────────────────────────────────
    static class RoundedPanel extends JPanel {
        private final int radius;
        RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            setBackground(bg);
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── Inner: Rounded line border ─────────────────────────────────────────────
    static class RoundedLineBorder extends AbstractBorder {
        private final Color color; private final int thickness, radius;
        RoundedLineBorder(Color c, int t, int r) { color = c; thickness = t; radius = r; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Double(x + 0.5, y + 0.5, w - 1, h - 1, radius, radius));
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(thickness, thickness, thickness, thickness); }
    }

    // ── Inner: Edge-line borders for header/footer ─────────────────────────────
    static class BottomBorder extends AbstractBorder {
        private final Color color; private final int thickness;
        BottomBorder(Color c, int t) { color = c; thickness = t; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.setColor(color);
            g.fillRect(x, y + h - thickness, w, thickness);
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(0, 0, thickness, 0); }
    }

    static class TopBorder extends AbstractBorder {
        private final Color color; private final int thickness;
        TopBorder(Color c, int t) { color = c; thickness = t; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.setColor(color);
            g.fillRect(x, y, w, thickness);
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(thickness, 0, 0, 0); }
    }
}