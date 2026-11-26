//ROLL NO:85,68,59,63.
package lostandfoundproject;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.List;

public class LostAndFoundFrame extends JFrame {
    // Premium Obsidian Rail + Warm Alabaster Workspace + Terracotta Accent
    private static final Color RAIL_BG = new Color(0x16, 0x16, 0x1A);
    private static final Color RAIL_CARD = new Color(0x21, 0x21, 0x27);
    private static final Color RAIL_BORDER = new Color(0x32, 0x32, 0x3B);
    private static final Color RAIL_TEXT = new Color(0xF5, 0xF3, 0xEE);
    private static final Color RAIL_MUTED = new Color(0xA1, 0xA1, 0xAA);
    private static final Color RAIL_INPUT_BG = new Color(0x1B, 0x1B, 0x20);

    private static final Color CANVAS_BG = new Color(0xF7, 0xF5, 0xF0);
    private static final Color SURFACE_BG = new Color(0xFF, 0xFF, 0xFD);
    private static final Color SURFACE_ALT = new Color(0xF1, 0xEE, 0xE6);
    private static final Color BORDER_LIGHT = new Color(0xE3, 0xDD, 0xD1);

    private static final Color INK_DARK = new Color(0x18, 0x18, 0x1B);
    private static final Color INK_MUTED = new Color(0x64, 0x64, 0x6E);

    private static final Color COPPER = new Color(0xC2, 0x59, 0x2A);
    private static final Color COPPER_HOVER = new Color(0xA4, 0x47, 0x1E);

    private static final Color OPEN_BG = new Color(0xE6, 0xF4, 0xEA);
    private static final Color OPEN_FG = new Color(0x13, 0x6F, 0x3A);
    private static final Color CLAIMED_BG = new Color(0xFE, 0xEC, 0xDC);
    private static final Color CLAIMED_FG = new Color(0x9A, 0x34, 0x12);

    private static final Font FONT_BRAND = new Font("SansSerif", Font.BOLD, 18);
    private static final Font FONT_SECTION = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FONT_LABEL_DARK = new Font("SansSerif", Font.BOLD, 11);
    private static final Font FONT_LABEL_LIGHT = new Font("SansSerif", Font.BOLD, 11);
    private static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 12);
    private static final Font FONT_KPI = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FONT_MONO_ID = new Font("Monospaced", Font.BOLD, 12);

    private final LostAndFoundRegistry registry = new LostAndFoundRegistry();

    // Left rail intake inputs
    private final JTextField titleField = createRailInput("e.g. Leather Cardholder");
    private final JComboBox<String> categoryBox = new JComboBox<>(
            new String[]{"Electronics", "ID & Cards", "Books & Notes", "Accessories", "Keys & Bags"});
    private final JTextField brandField = createRailInput("e.g. Apple AirPods Pro");
    private final JTextField locationField = createRailInput("e.g. North Reading Room");
    private final JTextField questionField = createRailInput("e.g. What initials are inside?");
    private final JTextField answerField = createRailInput("e.g. m.k.");

    // Right workspace search & stats
    private final JTextField searchField = createSurfaceInput("Filter by ID, title, category, or spot...");
    private final JCheckBox availableOnlyBox = new JCheckBox("Open items only");

    private final JLabel totalKpi = new JLabel("0");
    private final JLabel openKpi = new JLabel("0");
    private final JLabel claimedKpi = new JLabel("0");

    // Live inspector strip for selected item
    private final JLabel inspectorItemLabel = new JLabel("Select an item to preview its verification clue");
    private final JLabel inspectorMetaLabel = new JLabel("Location: —  •  Status: —");
    private final JLabel inspectorClueLabel = new JLabel("Clue Question: —");

    private final DefaultTableModel tableModel;
    private final JTable itemTable;

    public LostAndFoundFrame() {
        super("Campus Lost & Found — Concierge Desk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1140, 690);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null);

        String[] columns = {"ID", "Item", "Category", "Location", "Logged", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        itemTable = new JTable(tableModel);
        styleTable();

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(CANVAS_BG);
        root.add(buildLeftIntakeRail(), BorderLayout.WEST);
        root.add(buildMainWorkspace(), BorderLayout.CENTER);
        setContentPane(root);

        styleRailComboBox(categoryBox);
        updateBrandFieldState();
        categoryBox.addActionListener(e -> updateBrandFieldState());

        itemTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateInspector();
            }
        });

        refreshTable();
        updateInspector();
    }

    private JPanel buildLeftIntakeRail() {
        JPanel rail = new JPanel(new GridBagLayout());
        rail.setBackground(RAIL_BG);
        rail.setPreferredSize(new Dimension(340, 0));
        rail.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, RAIL_BORDER),
                new EmptyBorder(22, 20, 22, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JLabel kicker = new JLabel("CAMPUS CONCIERGE");
        kicker.setFont(new Font("SansSerif", Font.BOLD, 10));
        kicker.setForeground(COPPER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        rail.add(kicker, gbc);

        JLabel brandTitle = new JLabel("Lost & Found Desk");
        brandTitle.setFont(FONT_BRAND);
        brandTitle.setForeground(RAIL_TEXT);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        rail.add(brandTitle, gbc);

        JLabel sub = new JLabel("Register found items with a private verification clue.");
        sub.setFont(FONT_SMALL);
        sub.setForeground(RAIL_MUTED);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 18, 0);
        rail.add(sub, gbc);

        int y = 3;
        y = addRailField(rail, gbc, y, "ITEM TITLE *", titleField);
        y = addRailField(rail, gbc, y, "CATEGORY *", categoryBox);
        y = addRailField(rail, gbc, y, "BRAND / MODEL (ELECTRONICS)", brandField);
        y = addRailField(rail, gbc, y, "LOCATION FOUND *", locationField);
        y = addRailField(rail, gbc, y, "VERIFICATION QUESTION *", questionField);
        y = addRailField(rail, gbc, y, "EXPECTED SECRET ANSWER *", answerField);

        JButton addBtn = createCopperButton("Register Found Item");
        addBtn.addActionListener(e -> handleAddItem());
        gbc.gridy = y++;
        gbc.insets = new Insets(14, 0, 0, 0);
        rail.add(addBtn, gbc);

        // Push content cleanly to the top
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        gbc.gridy = y;
        gbc.weighty = 1.0;
        rail.add(spacer, gbc);

        return rail;
    }

    private int addRailField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(FONT_LABEL_LIGHT);
        lbl.setForeground(RAIL_MUTED);

        gbc.gridy = row;
        gbc.insets = new Insets(6, 0, 4, 0);
        panel.add(lbl, gbc);

        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel.add(field, gbc);

        return row + 2;
    }

    private JPanel buildMainWorkspace() {
        JPanel workspace = new JPanel(new BorderLayout(16, 16));
        workspace.setBackground(CANVAS_BG);
        workspace.setBorder(new EmptyBorder(20, 22, 20, 22));

        workspace.add(buildTopSummaryBar(), BorderLayout.NORTH);
        workspace.add(buildCenterVaultCard(), BorderLayout.CENTER);
        workspace.add(buildInspectorBar(), BorderLayout.SOUTH);

        return workspace;
    }

    private JPanel buildTopSummaryBar() {
        JPanel top = new JPanel(new BorderLayout(16, 10));
        top.setOpaque(false);

        JPanel headings = new JPanel();
        headings.setLayout(new BoxLayout(headings, BoxLayout.Y_AXIS));
        headings.setOpaque(false);

        JLabel pageTitle = new JLabel("Active Desk Ledger");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 19));
        pageTitle.setForeground(INK_DARK);

        JLabel pageDesc = new JLabel("Search logged items, inspect clue prompts, and verify ownership before release.");
        pageDesc.setFont(FONT_SMALL);
        pageDesc.setForeground(INK_MUTED);

        headings.add(pageTitle);
        headings.add(Box.createVerticalStrut(2));
        headings.add(pageDesc);

        JPanel kpiGrid = new JPanel(new GridLayout(1, 3, 12, 0));
        kpiGrid.setOpaque(false);
        kpiGrid.add(createKpiCard("TOTAL LOGGED", totalKpi, INK_DARK));
        kpiGrid.add(createKpiCard("AWAITING CLAIM", openKpi, OPEN_FG));
        kpiGrid.add(createKpiCard("VERIFIED & RETURNED", claimedKpi, COPPER));

        top.add(headings, BorderLayout.WEST);
        top.add(kpiGrid, BorderLayout.EAST);
        return top;
    }

    private JPanel createKpiCard(String title, JLabel numLabel, Color valueColor) {
        RoundedPanel card = new RoundedPanel(12, SURFACE_BG, BORDER_LIGHT);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel cap = new JLabel(title);
        cap.setFont(new Font("SansSerif", Font.BOLD, 10));
        cap.setForeground(INK_MUTED);
        cap.setAlignmentX(LEFT_ALIGNMENT);

        numLabel.setFont(FONT_KPI);
        numLabel.setForeground(valueColor);
        numLabel.setAlignmentX(LEFT_ALIGNMENT);

        card.add(cap);
        card.add(Box.createVerticalStrut(2));
        card.add(numLabel);
        return card;
    }

    private JPanel buildCenterVaultCard() {
        RoundedPanel card = new RoundedPanel(14, SURFACE_BG, BORDER_LIGHT);
        card.setLayout(new BorderLayout(12, 12));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel toolbar = new JPanel(new BorderLayout(10, 8));
        toolbar.setOpaque(false);

        JLabel sectionLabel = new JLabel("Found Belongings");
        sectionLabel.setFont(FONT_SECTION);
        sectionLabel.setForeground(INK_DARK);
        toolbar.add(sectionLabel, BorderLayout.WEST);

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightControls.setOpaque(false);

        searchField.setPreferredSize(new Dimension(235, 36));
        availableOnlyBox.setFont(FONT_SMALL);
        availableOnlyBox.setForeground(INK_DARK);
        availableOnlyBox.setOpaque(false);
        availableOnlyBox.setFocusPainted(false);

        JButton filterBtn = createOutlineButton("Apply Filter");
        filterBtn.addActionListener(e -> refreshTable());

        rightControls.add(searchField);
        rightControls.add(availableOnlyBox);
        rightControls.add(filterBtn);
        toolbar.add(rightControls, BorderLayout.EAST);

        searchField.addActionListener(e -> refreshTable());
        availableOnlyBox.addActionListener(e -> refreshTable());

        JScrollPane scroll = new JScrollPane(itemTable);
        scroll.setBorder(new LineBorder(BORDER_LIGHT, 1, true));
        scroll.getViewport().setBackground(SURFACE_BG);

        card.add(toolbar, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildInspectorBar() {
        RoundedPanel bar = new RoundedPanel(14, SURFACE_BG, BORDER_LIGHT);
        bar.setLayout(new BorderLayout(16, 8));
        bar.setBorder(new EmptyBorder(14, 18, 14, 18));

        JPanel leftInfo = new JPanel();
        leftInfo.setLayout(new BoxLayout(leftInfo, BoxLayout.Y_AXIS));
        leftInfo.setOpaque(false);

        JLabel tag = new JLabel("SELECTED ITEM INSPECTOR");
        tag.setFont(new Font("SansSerif", Font.BOLD, 10));
        tag.setForeground(COPPER);
        tag.setAlignmentX(LEFT_ALIGNMENT);

        inspectorItemLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        inspectorItemLabel.setForeground(INK_DARK);
        inspectorItemLabel.setAlignmentX(LEFT_ALIGNMENT);

        inspectorMetaLabel.setFont(FONT_SMALL);
        inspectorMetaLabel.setForeground(INK_MUTED);
        inspectorMetaLabel.setAlignmentX(LEFT_ALIGNMENT);

        inspectorClueLabel.setFont(FONT_BODY);
        inspectorClueLabel.setForeground(INK_DARK);
        inspectorClueLabel.setAlignmentX(LEFT_ALIGNMENT);

        leftInfo.add(tag);
        leftInfo.add(Box.createVerticalStrut(3));
        leftInfo.add(inspectorItemLabel);
        leftInfo.add(Box.createVerticalStrut(2));
        leftInfo.add(inspectorMetaLabel);
        leftInfo.add(Box.createVerticalStrut(6));
        leftInfo.add(inspectorClueLabel);

        JPanel actionGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionGroup.setOpaque(false);

        JButton clueBtn = createOutlineButton("Clue Pop-up");
        clueBtn.addActionListener(e -> handleInspectClue());

        JButton verifyBtn = createCopperButton("Verify & Release Item");
        verifyBtn.addActionListener(e -> handleVerifyAndClaim());

        actionGroup.add(clueBtn);
        actionGroup.add(verifyBtn);

        bar.add(leftInfo, BorderLayout.CENTER);
        bar.add(actionGroup, BorderLayout.EAST);
        return bar;
    }

    private void styleTable() {
        itemTable.setFont(FONT_BODY);
        itemTable.setForeground(INK_DARK);
        itemTable.setBackground(SURFACE_BG);
        itemTable.setRowHeight(36);
        itemTable.setShowHorizontalLines(true);
        itemTable.setShowVerticalLines(false);
        itemTable.setGridColor(SURFACE_ALT);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemTable.setSelectionBackground(new Color(0xFB, 0xEF, 0xE8));
        itemTable.setSelectionForeground(INK_DARK);

        itemTable.getTableHeader().setFont(FONT_LABEL_DARK);
        itemTable.getTableHeader().setForeground(INK_MUTED);
        itemTable.getTableHeader().setBackground(SURFACE_ALT);
        itemTable.getTableHeader().setPreferredSize(new Dimension(0, 36));
        itemTable.getTableHeader().setReorderingAllowed(false);

        itemTable.getColumnModel().getColumn(0).setPreferredWidth(82);
        itemTable.getColumnModel().getColumn(1).setPreferredWidth(270);
        itemTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        itemTable.getColumnModel().getColumn(3).setPreferredWidth(175);
        itemTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        itemTable.getColumnModel().getColumn(5).setPreferredWidth(145);

        itemTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                                                                  boolean isSelected, boolean hasFocus,
                                                                  int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 12, 0, 12));

                if (column == 0) {
                    setFont(FONT_MONO_ID);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setForeground(isSelected ? COPPER : INK_DARK);
                    setBackground(isSelected ? table.getSelectionBackground() : SURFACE_BG);
                } else if (column == 5 && value != null) {
                    String text = value.toString();
                    boolean isClaimed = text.startsWith("Claimed");
                    setFont(FONT_LABEL_DARK);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setBackground(isClaimed ? CLAIMED_BG : OPEN_BG);
                    setForeground(isClaimed ? CLAIMED_FG : OPEN_FG);
                } else {
                    setFont(FONT_BODY);
                    setHorizontalAlignment(SwingConstants.LEFT);
                    if (isSelected) {
                        setBackground(table.getSelectionBackground());
                        setForeground(table.getSelectionForeground());
                    } else {
                        setBackground(row % 2 == 0 ? SURFACE_BG : new Color(0xFC, 0xFA, 0xF6));
                        setForeground(INK_DARK);
                    }
                }
                return this;
            }
        });
    }

    private JTextField createRailInput(String hint) {
        JTextField tf = new JTextField(18);
        tf.setFont(FONT_BODY);
        tf.setForeground(RAIL_TEXT);
        tf.setBackground(RAIL_INPUT_BG);
        tf.setCaretColor(COPPER);
        tf.setToolTipText(hint);
        tf.setBorder(new CompoundBorder(
                new LineBorder(RAIL_BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        return tf;
    }

    private JTextField createSurfaceInput(String hint) {
        JTextField tf = new JTextField(18);
        tf.setFont(FONT_BODY);
        tf.setForeground(INK_DARK);
        tf.setBackground(SURFACE_BG);
        tf.setCaretColor(COPPER);
        tf.setToolTipText(hint);
        tf.setBorder(new CompoundBorder(
                new LineBorder(BORDER_LIGHT, 1, true),
                new EmptyBorder(8, 11, 8, 11)));
        return tf;
    }

    private void styleRailComboBox(JComboBox<String> box) {
        box.setFont(FONT_BODY);
        box.setForeground(RAIL_TEXT);
        box.setBackground(RAIL_CARD);
        box.setBorder(new LineBorder(RAIL_BORDER, 1, true));
    }

    private JButton createCopperButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(!isEnabled() ? BORDER_LIGHT : (getModel().isRollover() ? COPPER_HOVER : COPPER));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        return btn;
    }

    private JButton createOutlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? SURFACE_ALT : SURFACE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER_LIGHT);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(INK_DARK);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 15, 9, 15));
        return btn;
    }

    private void updateBrandFieldState() {
        boolean isTech = "Electronics".equals(categoryBox.getSelectedItem());
        brandField.setEnabled(isTech);
        brandField.setBackground(isTech ? RAIL_INPUT_BG : RAIL_CARD);
        if (!isTech) {
            brandField.setText("");
        }
    }

    private void handleAddItem() {
        String title = titleField.getText().trim();
        String category = (String) categoryBox.getSelectedItem();
        String brand = brandField.getText().trim();
        String location = locationField.getText().trim();
        String question = questionField.getText().trim();
        String answer = answerField.getText().trim();

        if (title.isEmpty() || location.isEmpty() || question.isEmpty() || answer.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please complete all required (*) fields before logging the item.",
                    "Incomplete Entry", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Item created;
        if ("Electronics".equals(category)) {
            created = registry.addElectronicsItem(title, location,
                    brand.isEmpty() ? "Unspecified" : brand, question, answer);
        } else {
            created = registry.addGeneralItem(title, category, location, question, answer);
        }

        titleField.setText("");
        brandField.setText("");
        locationField.setText("");
        questionField.setText("");
        answerField.setText("");

        refreshTable();
        selectItemRow(created.getItemId());
    }

    private void selectItemRow(String itemId) {
        for (int r = 0; r < tableModel.getRowCount(); r++) {
            if (itemId.equals(tableModel.getValueAt(r, 0))) {
                itemTable.setRowSelectionInterval(r, r);
                break;
            }
        }
        updateInspector();
    }

    private Item getSelectedItem() {
        int row = itemTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        String itemId = (String) tableModel.getValueAt(row, 0);
        return registry.findById(itemId);
    }

    private void updateInspector() {
        Item item = getSelectedItem();
        if (item == null) {
            inspectorItemLabel.setText("Select an item to preview its verification clue");
            inspectorMetaLabel.setText("Location: —  •  Date Logged: —  •  Status: —");
            inspectorClueLabel.setText("Clue Question: —");
            return;
        }
        inspectorItemLabel.setText(item.getItemId() + "  —  " + item.getItemBadge());
        inspectorMetaLabel.setText("Location: " + item.getLocationFound()
                + "  •  Logged: " + item.getDateReported()
                + "  •  Status: " + item.getStatusText());
        inspectorClueLabel.setText("Clue Question: \"" + item.getVerificationQuestion() + "\"");
    }

    private void handleInspectClue() {
        Item item = getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this,
                    "Click a row in the table first to inspect its clue.",
                    "No Item Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
                item.getItemBadge() +
                        "\nFound Location: " + item.getLocationFound() +
                        "\nStatus: " + item.getStatusText() +
                        "\n\nVerification Question:\n\"" + item.getVerificationQuestion() + "\"",
                "Verification Clue — " + item.getItemId(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleVerifyAndClaim() {
        Item item = getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this,
                    "Select an item from the table before starting verification.",
                    "No Item Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (item.isClaimed()) {
            JOptionPane.showMessageDialog(this,
                    "This item has already been claimed by Student ID " + item.getClaimedByStudentId() + ".",
                    "Already Returned", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ConciergeVerifyDialog dialog = new ConciergeVerifyDialog(this, item);
        dialog.setVisible(true);
        if (dialog.isApproved()) {
            refreshTable();
            selectItemRow(item.getItemId());
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Item> filtered = registry.getFilteredItems(searchField.getText(), availableOnlyBox.isSelected());
        for (Item item : filtered) {
            tableModel.addRow(new Object[]{
                    item.getItemId(),
                    item.getItemBadge(),
                    item.getCategory(),
                    item.getLocationFound(),
                    item.getDateReported(),
                    item.getStatusText()
            });
        }

        totalKpi.setText(String.valueOf(registry.getTotalCount()));
        openKpi.setText(String.valueOf(registry.getAvailableCount()));
        claimedKpi.setText(String.valueOf(registry.getClaimedCount()));
        updateInspector();
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fillColor;
        private final Color strokeColor;

        RoundedPanel(int radius, Color fillColor, Color strokeColor) {
            this.radius = radius;
            this.fillColor = fillColor;
            this.strokeColor = strokeColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(strokeColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private class ConciergeVerifyDialog extends JDialog {
        private boolean approved = false;

        ConciergeVerifyDialog(JFrame owner, Item item) {
            super(owner, "Verify Ownership — " + item.getItemId(), true);
            setSize(460, 320);
            setLocationRelativeTo(owner);

            JPanel container = new JPanel();
            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
            container.setBackground(CANVAS_BG);
            container.setBorder(new EmptyBorder(20, 22, 20, 22));

            RoundedPanel promptCard = new RoundedPanel(12, SURFACE_BG, BORDER_LIGHT);
            promptCard.setLayout(new BoxLayout(promptCard, BoxLayout.Y_AXIS));
            promptCard.setBorder(new EmptyBorder(12, 14, 12, 14));
            promptCard.setAlignmentX(LEFT_ALIGNMENT);
            promptCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

            JLabel itemName = new JLabel(item.getItemBadge());
            itemName.setFont(FONT_SECTION);
            itemName.setForeground(INK_DARK);
            itemName.setAlignmentX(LEFT_ALIGNMENT);

            JLabel clueText = new JLabel("<html><span style='color:#C2592A;font-weight:bold;'>Clue:</span> "
                    + item.getVerificationQuestion() + "</html>");
            clueText.setFont(FONT_BODY);
            clueText.setForeground(INK_DARK);
            clueText.setAlignmentX(LEFT_ALIGNMENT);

            promptCard.add(itemName);
            promptCard.add(Box.createVerticalStrut(4));
            promptCard.add(clueText);

            JLabel idLbl = new JLabel("CLAIMANT STUDENT ID *");
            idLbl.setFont(FONT_LABEL_DARK);
            idLbl.setForeground(INK_MUTED);
            idLbl.setAlignmentX(LEFT_ALIGNMENT);

            JTextField idField = createSurfaceInput("e.g. CS-2026-104");
            idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            idField.setAlignmentX(LEFT_ALIGNMENT);

            JLabel ansLbl = new JLabel("CLAIMANT'S VERIFICATION ANSWER *");
            ansLbl.setFont(FONT_LABEL_DARK);
            ansLbl.setForeground(INK_MUTED);
            ansLbl.setAlignmentX(LEFT_ALIGNMENT);

            JTextField ansField = createSurfaceInput("Enter answer to match desk record");
            ansField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            ansField.setAlignmentX(LEFT_ALIGNMENT);

            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            btnRow.setOpaque(false);
            btnRow.setAlignmentX(LEFT_ALIGNMENT);
            btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

            JButton cancelBtn = createOutlineButton("Cancel");
            cancelBtn.addActionListener(e -> dispose());

            JButton approveBtn = createCopperButton("Verify & Release");
            approveBtn.addActionListener(e -> {
                String studentId = idField.getText().trim();
                String answer = ansField.getText().trim();

                if (studentId.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter the claimant's Student ID.",
                            "Student ID Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (item.markClaimed(studentId, answer)) {
                    approved = true;
                    JOptionPane.showMessageDialog(this,
                            "Ownership verified! Item " + item.getItemId()
                                    + " released to " + studentId.toUpperCase() + ".",
                            "Release Approved", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Verification failed. The answer does not match the desk record.",
                            "Mismatch", JOptionPane.ERROR_MESSAGE);
                }
            });

            btnRow.add(cancelBtn);
            btnRow.add(approveBtn);

            container.add(promptCard);
            container.add(Box.createVerticalStrut(12));
            container.add(idLbl);
            container.add(Box.createVerticalStrut(4));
            container.add(idField);
            container.add(Box.createVerticalStrut(10));
            container.add(ansLbl);
            container.add(Box.createVerticalStrut(4));
            container.add(ansField);
            container.add(Box.createVerticalStrut(14));
            container.add(btnRow);

            setContentPane(container);
        }

        boolean isApproved() {
            return approved;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new LostAndFoundFrame().setVisible(true));
    }
}