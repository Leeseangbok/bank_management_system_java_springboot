package com.bank.enterprise.ui.panels;

import com.bank.enterprise.ui.BankDesktopApp;
import com.bank.enterprise.ui.api.BankApiClient;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

import static com.bank.enterprise.ui.components.UIStyles.*;

public class OpenAccountPanel extends JPanel {

    // ── Account type model ────────────────────────────────────────────────────
    public static class KeyValue {
        @Getter private final int id;
        private final String name;
        private final String description;
        private final String icon;

        public KeyValue(int id, String name, String description, String icon) {
            this.id = id; this.name = name;
            this.description = description; this.icon = icon;
        }
        @Override public String toString() { return name; }
    }

    private static final KeyValue[] ACCOUNT_TYPES = {
            new KeyValue(1, "Savings",       "Earn interest on your balance",      "💰"),
            new KeyValue(2, "Current",       "Day-to-day spending & withdrawals",  "💳"),
            new KeyValue(3, "Fixed Deposit", "Lock in a rate for a set term",      "🔒"),
            new KeyValue(4, "Business",      "Manage your company's finances",     "🏢"),
            new KeyValue(5, "Loan",          "Borrow funds at a competitive rate", "📋"),
    };

    // ── Wizard state ──────────────────────────────────────────────────────────
    private KeyValue selectedType    = ACCOUNT_TYPES[0];
    private final TypeCard[] typeCards = new TypeCard[ACCOUNT_TYPES.length];

    // Step-2 PIN fields
    private JPasswordField pinField;
    private JPasswordField pinConfirmField;
    private JLabel pinHintLabel;
    private JLabel pinMatchLabel;
    private JPanel[] pinDots;          // 4 dot indicators
    private JLabel selectedTypeLabel;  // recap of chosen type in step 2

    // Shared footer controls
    private JLabel  statusLabel;
    private JButton primaryButton;     // "Next" on step 1, "Open Account" on step 2
    private JButton backButton;

    // Step tracker
    private static final String STEP_TYPE = "TYPE";
    private static final String STEP_PIN  = "PIN";
    private JPanel stepsContainer;
    private CardLayout stepsLayout;
    private String currentStep = STEP_TYPE;

    // Step indicator labels
    private JLabel stepDot1, stepDot2;

    // ══════════════════════════════════════════════════════════════════════════
    public OpenAccountPanel(BankDesktopApp parentApp) {
        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildHeader(),          BorderLayout.NORTH);
        add(buildStepsContainer(),  BorderLayout.CENTER);
        add(buildFooter(parentApp), BorderLayout.SOUTH);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setBackground(SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
                new BottomBorder(DIVIDER, 1),
                BorderFactory.createEmptyBorder(18, 28, 18, 28)
        ));

        JLabel brand = new JLabel("● ENTERPRISE BANK");
        brand.setFont(FONT_BRAND);
        brand.setForeground(ACCENT_GOLD);

        JLabel title = new JLabel("Open a New Account");
        title.setFont(FONT_HEADLINE);
        title.setForeground(TEXT_PRIMARY);

        JLabel sub = new JLabel("Complete both steps to activate your account");
        sub.setFont(FONT_SUBHEAD);
        sub.setForeground(TEXT_MUTED);

        JPanel stack = new JPanel();
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.setBackground(SURFACE);
        stack.add(brand);
        stack.add(Box.createVerticalStrut(6));
        stack.add(title);
        stack.add(Box.createVerticalStrut(2));
        stack.add(sub);

        // Step indicator (right side of header)
        JPanel stepIndicator = buildStepIndicator();

        header.add(stack,         BorderLayout.WEST);
        header.add(stepIndicator, BorderLayout.EAST);
        return header;
    }

    private JPanel buildStepIndicator() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panel.setBackground(SURFACE);

        stepDot1 = makeStepDot("1  Account Type", true);
        JLabel connector = new JLabel("──");
        connector.setForeground(DIVIDER);
        stepDot2 = makeStepDot("2  Set PIN", false);

        panel.add(stepDot1);
        panel.add(connector);
        panel.add(stepDot2);
        return panel;
    }

    private JLabel makeStepDot(String text, boolean active) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 12));
        lbl.setForeground(active ? ACCENT_GOLD : TEXT_MUTED);
        return lbl;
    }

    // ── Step container (CardLayout) ───────────────────────────────────────────
    private JPanel buildStepsContainer() {
        stepsLayout = new CardLayout();
        stepsContainer = new JPanel(stepsLayout);
        stepsContainer.setBackground(BG);

        stepsContainer.add(buildTypeStep(), STEP_TYPE);
        stepsContainer.add(buildPinStep(),  STEP_PIN);

        stepsLayout.show(stepsContainer, STEP_TYPE);
        return stepsContainer;
    }

    // ── Step 1: Account type grid ─────────────────────────────────────────────
    private JPanel buildTypeStep() {
        JPanel grid = new JPanel(new GridLayout(0, 2, 14, 14));
        grid.setBackground(BG);
        grid.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        for (int i = 0; i < ACCOUNT_TYPES.length; i++) {
            TypeCard card = new TypeCard(ACCOUNT_TYPES[i]);
            typeCards[i] = card;
            grid.add(card);
        }
        if (ACCOUNT_TYPES.length % 2 != 0) {
            JPanel filler = new JPanel();
            filler.setBackground(BG);
            grid.add(filler);
        }
        typeCards[0].setSelected(true);

        JScrollPane scroll = new JScrollPane(wrapCenter(grid));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(BG);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    // ── Step 2: PIN entry ─────────────────────────────────────────────────────
    private JPanel buildPinStep() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG);

        // Card container
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(DIVIDER, 1, 14),
                BorderFactory.createEmptyBorder(36, 44, 36, 44)
        ));
        card.setMaximumSize(new Dimension(420, 9999));

        // Recap pill: shows which account type was selected
        selectedTypeLabel = new JLabel("● Savings");
        selectedTypeLabel.setFont(FONT_LABEL);
        selectedTypeLabel.setForeground(ACCENT_GOLD);
        selectedTypeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pinTitle = new JLabel("Set your account PIN");
        pinTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pinTitle.setForeground(TEXT_PRIMARY);
        pinTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pinSub = new JLabel("Choose a 4-digit PIN you'll use to authorise transactions");
        pinSub.setFont(FONT_SUBHEAD);
        pinSub.setForeground(TEXT_MUTED);
        pinSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Dot indicators (visual progress for 4-digit PIN)
        JPanel dotsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        dotsRow.setBackground(new Color(0,0,0,0));
        dotsRow.setOpaque(false);
        dotsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        pinDots = new JPanel[4];
        for (int i = 0; i < 4; i++) {
            pinDots[i] = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Boolean filled = (Boolean) getClientProperty("filled");

                    g2.setColor(Boolean.TRUE.equals(filled)
                            ? ACCENT_GOLD
                            : FIELD_BORDER);
                    g2.fillOval(0, 0, 14, 14);
                    g2.dispose();
                }
            };
            pinDots[i].setPreferredSize(new Dimension(14, 14));
            pinDots[i].setOpaque(false);
            pinDots[i].putClientProperty("filled", false);
            dotsRow.add(pinDots[i]);
        }

        // PIN field
        pinField = new JPasswordField(20);
        styleField(pinField);
        pinField.setFont(FONT_MONO);
        pinField.setHorizontalAlignment(JTextField.CENTER);
        ((AbstractDocument) pinField.getDocument()).setDocumentFilter(new PinFilter(4));

        pinHintLabel = new JLabel(" ");
        pinHintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pinHintLabel.setForeground(TEXT_MUTED);
        pinHintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Confirm PIN field
        JLabel confirmLabel = makeFieldLabel("Confirm PIN");
        pinConfirmField = new JPasswordField(20);
        styleField(pinConfirmField);
        pinConfirmField.setFont(FONT_MONO);
        pinConfirmField.setHorizontalAlignment(JTextField.CENTER);
        ((AbstractDocument) pinConfirmField.getDocument()).setDocumentFilter(new PinFilter(4));

        pinMatchLabel = new JLabel(" ");
        pinMatchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pinMatchLabel.setForeground(DANGER);
        pinMatchLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Live dot + hint update on PIN field
        pinField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { updatePinFeedback(); }
            public void removeUpdate(DocumentEvent e)  { updatePinFeedback(); }
            public void changedUpdate(DocumentEvent e) { updatePinFeedback(); }
        });

        // Live match check on confirm field
        pinConfirmField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { updateMatchFeedback(); }
            public void removeUpdate(DocumentEvent e)  { updateMatchFeedback(); }
            public void changedUpdate(DocumentEvent e) { updateMatchFeedback(); }
        });

        // Assemble card
        card.add(selectedTypeLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(pinTitle);
        card.add(Box.createVerticalStrut(4));
        card.add(pinSub);
        card.add(Box.createVerticalStrut(24));
        card.add(dotsRow);
        card.add(Box.createVerticalStrut(12));
        card.add(makeFieldLabel("PIN  (4 digits)"));
        card.add(Box.createVerticalStrut(6));
        card.add(pinField);
        card.add(Box.createVerticalStrut(4));
        card.add(pinHintLabel);
        card.add(Box.createVerticalStrut(18));
        card.add(confirmLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(pinConfirmField);
        card.add(Box.createVerticalStrut(4));
        card.add(pinMatchLabel);

        outer.add(card);
        return outer;
    }

    // ── Footer ────────────────────────────────────────────────────────────────
    private JPanel buildFooter(BankDesktopApp parentApp) {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(SURFACE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                new TopBorder(DIVIDER, 1),
                BorderFactory.createEmptyBorder(14, 28, 14, 28)
        ));

        statusLabel   = new JLabel(" ");
        statusLabel.setFont(FONT_SUBHEAD);
        statusLabel.setForeground(TEXT_MUTED);

        backButton    = makeGhostButton();
        primaryButton = makeSolidButton(new Color(0x0D1B2A));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setBackground(SURFACE);
        actions.add(backButton);
        actions.add(primaryButton);

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(actions,     BorderLayout.EAST);

        // Back: navigate between steps or exit
        backButton.addActionListener(e -> {
            if (currentStep.equals(STEP_PIN)) {
                goToStep(STEP_TYPE);
            } else {
                parentApp.navigateTo("DASHBOARD");
            }
        });

        // Primary: advance step or submit
        primaryButton.addActionListener(e -> {
            if (currentStep.equals(STEP_TYPE)) {
                // Move to PIN step, update recap label
                selectedTypeLabel.setText("● " + selectedType.name);
                pinField.setText("");
                pinConfirmField.setText("");
                pinHintLabel.setText(" ");
                pinMatchLabel.setText(" ");
                resetDots();
                goToStep(STEP_PIN);
            } else {
                attemptSubmit(parentApp);
            }
        });

        return footer;
    }

    // ── Step navigation ───────────────────────────────────────────────────────
    private void goToStep(String step) {
        currentStep = step;
        stepsLayout.show(stepsContainer, step);

        boolean onPin = step.equals(STEP_PIN);

        // Update step indicator style
        stepDot1.setFont(new Font("Segoe UI", onPin ? Font.PLAIN : Font.BOLD, 12));
        stepDot1.setForeground(onPin ? TEXT_MUTED : ACCENT_GOLD);
        stepDot2.setFont(new Font("Segoe UI", onPin ? Font.BOLD : Font.PLAIN, 12));
        stepDot2.setForeground(onPin ? ACCENT_GOLD : TEXT_MUTED);

        // Update footer controls
        backButton.setText(onPin ? "← Account Type" : "← Dashboard");
        primaryButton.setText(onPin ? "Open Account" : "Next  →");
        statusLabel.setText(" ");

        if (onPin) {
            SwingUtilities.invokeLater(() -> pinField.requestFocusInWindow());
        }
    }

    // ── Submit ────────────────────────────────────────────────────────────────
    private void attemptSubmit(BankDesktopApp parentApp) {
        String pin        = new String(pinField.getPassword());
        String pinConfirm = new String(pinConfirmField.getPassword());

        if (pin.length() != 4) {
            statusLabel.setForeground(DANGER);
            statusLabel.setText("✕ PIN must be exactly 4 digits.");
            return;
        }
        if (!pin.equals(pinConfirm)) {
            statusLabel.setForeground(DANGER);
            statusLabel.setText("✕ PINs do not match.");
            pinMatchLabel.setText("PINs do not match");
            return;
        }

        primaryButton.setEnabled(false);
        primaryButton.setText("Opening…");
        statusLabel.setText(" ");

        final String finalPin = pin;
        SwingUtilities.invokeLater(() -> {
            try {
                boolean success = BankApiClient.getInstance()
                        .openNewAccount(selectedType.getId(), finalPin);

                if (success) {
                    statusLabel.setForeground(POSITIVE);
                    statusLabel.setText("✓ Account opened successfully!");
                    Timer nav = new Timer(900, ev -> parentApp.navigateTo("DASHBOARD"));
                    nav.setRepeats(false);
                    nav.start();
                } else {
                    statusLabel.setForeground(DANGER);
                    statusLabel.setText("✕ Could not open account — please try again.");
                    primaryButton.setEnabled(true);
                    primaryButton.setText("Open Account");
                }
            } catch (Exception ex) {
                statusLabel.setForeground(DANGER);
                statusLabel.setText("✕ Network error: " + ex.getMessage());
                primaryButton.setEnabled(true);
                primaryButton.setText("Open Account");
            }
        });
    }

    // ── PIN feedback ──────────────────────────────────────────────────────────
    private void updatePinFeedback() {
        String pin = new String(pinField.getPassword());
        int len = pin.length();

        // Fill dots
        for (int i = 0; i < pinDots.length; i++) {
            pinDots[i].putClientProperty("filled", i < len);
            pinDots[i].repaint();
        }

        // Hint text
        if (len == 0) {
            pinHintLabel.setForeground(TEXT_MUTED);
            pinHintLabel.setText("Enter 4 digits");
        } else if (len < 4) {
            pinHintLabel.setForeground(PIN_WEAK);
            pinHintLabel.setText((4 - len) + " more digit" + (4 - len == 1 ? "" : "s") + " needed");
        } else {
            // Check for trivial PINs
            if (isTrivialPin(pin)) {
                pinHintLabel.setForeground(PIN_FAIR);
                pinHintLabel.setText("Avoid simple PINs like 1234 or 0000");
            } else {
                pinHintLabel.setForeground(PIN_STRONG);
                pinHintLabel.setText("✓ PIN looks good");
            }
        }
        if (pinConfirmField.getPassword().length > 0) updateMatchFeedback();
    }

    private void updateMatchFeedback() {
        String pin     = new String(pinField.getPassword());
        String confirm = new String(pinConfirmField.getPassword());
        if (confirm.isEmpty()) {
            pinMatchLabel.setText(" ");
            return;
        }
        if (pin.equals(confirm)) {
            pinMatchLabel.setForeground(PIN_STRONG);
            pinMatchLabel.setText("✓ PINs match");
        } else {
            pinMatchLabel.setForeground(DANGER);
            pinMatchLabel.setText("PINs do not match");
        }
    }

    private boolean isTrivialPin(String pin) {
        String[] trivial = {"0000","1111","2222","3333","4444","5555","6666","7777","8888","9999",
                "1234","4321","0123","9876"};
        for (String t : trivial) if (t.equals(pin)) return true;
        return false;
    }

    private void resetDots() {
        for (JPanel dot : pinDots) {
            dot.putClientProperty("filled", false);
            dot.repaint();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JPanel wrapCenter(JPanel inner) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1; gbc.weighty = 1;
        wrapper.add(inner, gbc);
        return wrapper;
    }

    private JLabel makeFieldLabel(String text) {
        JLabel lbl = new JLabel(text.toUpperCase());
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void styleField(JTextField field) {
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT_GOLD);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(FIELD_BORDER, 1, 8),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedLineBorder(ACCENT_GOLD, 1, 8),
                        BorderFactory.createEmptyBorder(10, 14, 10, 14)));
            }
            @Override public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedLineBorder(FIELD_BORDER, 1, 8),
                        BorderFactory.createEmptyBorder(10, 14, 10, 14)));
            }
        });
    }

    // ── DocumentFilter: digits only, max length ───────────────────────────────
    static class PinFilter extends javax.swing.text.DocumentFilter {
        private final int maxLen;
        PinFilter(int maxLen) { this.maxLen = maxLen; }

        @Override
        public void insertString(FilterBypass fb, int off, String str, javax.swing.text.AttributeSet a)
                throws javax.swing.text.BadLocationException {
            if (str == null) return;
            String digits = str.replaceAll("[^0-9]", "");
            if (fb.getDocument().getLength() + digits.length() <= maxLen)
                super.insertString(fb, off, digits, a);
        }

        @Override
        public void replace(FilterBypass fb, int off, int len, String str, javax.swing.text.AttributeSet a)
                throws javax.swing.text.BadLocationException {
            if (str == null) return;
            String digits = str.replaceAll("[^0-9]", "");
            if (fb.getDocument().getLength() - len + digits.length() <= maxLen)
                super.replace(fb, off, len, digits, a);
        }
    }

    // ── TypeCard ──────────────────────────────────────────────────────────────
    private class TypeCard extends JPanel {
        private boolean selected = false;

        TypeCard(KeyValue type) {
            setLayout(new BorderLayout(0, 8));
            setBackground(CARD_BG);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
            setPreferredSize(new Dimension(240, 110));

            JLabel iconLabel = new JLabel(type.icon + "  " + type.name.toUpperCase());
            iconLabel.setFont(FONT_CARD_TTL);
            iconLabel.setForeground(TEXT_PRIMARY);

            JLabel descLabel = new JLabel("<html>" + type.description + "</html>");
            descLabel.setFont(FONT_CARD_SUB);
            descLabel.setForeground(TEXT_MUTED);

            JLabel check = new JLabel("✓");
            check.setFont(new Font("Segoe UI", Font.BOLD, 16));
            check.setForeground(ACCENT_GOLD);
            check.setVisible(false);

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.add(iconLabel, BorderLayout.WEST);
            top.add(check,     BorderLayout.EAST);

            add(top,       BorderLayout.NORTH);
            add(descLabel, BorderLayout.CENTER);

            putClientProperty("check", check);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    for (TypeCard c : typeCards) c.setSelected(false);
                    setSelected(true);
                    selectedType = type;
                }
                @Override public void mouseEntered(MouseEvent e) { if (!selected) repaint(); }
                @Override public void mouseExited(MouseEvent e)  { if (!selected) repaint(); }
            });
        }

        void setSelected(boolean sel) {
            this.selected = sel;
            JLabel check = (JLabel) getClientProperty("check");
            if (check != null) check.setVisible(sel);
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill   = selected ? new Color(0x213348)
                    : getMousePosition() != null ? new Color(0x1E2F44) : CARD_BG;
            Color border = selected ? ACCENT_GOLD : FIELD_BORDER;
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.setColor(border);
            g2.setStroke(new BasicStroke(selected ? 1.5f : 1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            if (selected) {
                g2.setColor(ACCENT_GOLD);
                g2.setStroke(new BasicStroke(3f));
                g2.drawLine(1, 10, 1, getHeight() - 10);
            }
            g2.dispose();
        }
    }

    // ── Button factories ──────────────────────────────────────────────────────
    private JButton makeSolidButton(Color fg) {
        JButton btn = new JButton("Next  →") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled()
                        ? (getModel().isRollover() ? com.bank.enterprise.ui.components.UIStyles.ACCENT_GOLD.darker() : com.bank.enterprise.ui.components.UIStyles.ACCENT_GOLD)
                        : com.bank.enterprise.ui.components.UIStyles.ACCENT_GOLD.darker().darker());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        return btn;
    }

    private JButton makeGhostButton() {
        JButton btn = new JButton("← Dashboard") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(com.bank.enterprise.ui.components.UIStyles.ACCENT_BLUE.getRed(), com.bank.enterprise.ui.components.UIStyles.ACCENT_BLUE.getGreen(), com.bank.enterprise.ui.components.UIStyles.ACCENT_BLUE.getBlue(), 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(com.bank.enterprise.ui.components.UIStyles.ACCENT_BLUE);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(com.bank.enterprise.ui.components.UIStyles.ACCENT_BLUE, 1, 8),
                BorderFactory.createEmptyBorder(7, 16, 7, 16)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        return btn;
    }

    // ── Inner borders ──────────────────────────────────────────────────────────
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

    static class BottomBorder extends AbstractBorder {
        private final Color color; private final int thickness;
        BottomBorder(Color c, int t) { color = c; thickness = t; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.setColor(color); g.fillRect(x, y + h - thickness, w, thickness);
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(0, 0, thickness, 0); }
    }

    static class TopBorder extends AbstractBorder {
        private final Color color; private final int thickness;
        TopBorder(Color c, int t) { color = c; thickness = t; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.setColor(color); g.fillRect(x, y, w, thickness);
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(thickness, 0, 0, 0); }
    }
}