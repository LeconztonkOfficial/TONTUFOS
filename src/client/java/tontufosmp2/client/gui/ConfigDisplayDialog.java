package tontufosmp2.client.gui;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import tontufosmp2.net.ModMessages;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;

public class ConfigDisplayDialog extends JDialog {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private Point initialClick;

    public ConfigDisplayDialog(Frame owner, Map<String, String> configData) {
        super(owner, "Visor/Editor de Configuración", true);

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setSize(700, 550);
        setLocationRelativeTo(owner);

        // --- Panel con fondo ---
        JPanel backgroundPanel = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(40, 45, 50, 230), 0, getHeight(), new Color(60, 65, 75, 240));
                g2d.setPaint(gp);
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                g2d.setColor(new Color(255, 255, 255, 70));
                g2d.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
                g2d.dispose();
            }
        };
        backgroundPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(backgroundPanel);

        // --- Barra de Título
        JPanel titleBar = createTitleBar();

        // --- Modelo de Tabla
        String[] columnNames = {"Propiedad", "Valor"};
        this.tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0) return false;
                String propertyName = (String) getValueAt(row, 0);
                return !"nivelesAcumulacion".equals(propertyName);
            }
        };
        for (Map.Entry<String, String> entry : configData.entrySet()) {
            tableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }

        // --- Tabla con Estilo
        this.table = createStyledTable();
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 50)));

        // --- Panel de Botones
        JPanel buttonPanel = createButtonPanel();

        // --- Añadir Componentes
        backgroundPanel.add(titleBar, BorderLayout.NORTH);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JTable createStyledTable() {
        JTable styledTable = new JTable(tableModel);
        styledTable.setOpaque(false);
        styledTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        styledTable.setGridColor(new Color(255, 255, 255, 50));
        styledTable.setForeground(new Color(220, 220, 220));
        
        // CellRenderer  para el estilo de las celdas
        TableCellRenderer customRenderer = new AeroCellRenderer();
        styledTable.setDefaultRenderer(Object.class, customRenderer);
        
        JTableHeader header = styledTable.getTableHeader();
        header.setOpaque(false);
        header.setBackground(new Color(0, 0, 0, 80));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 50)));

        JTextField editorField = new JTextField();
        editorField.setOpaque(false);
        editorField.setForeground(Color.WHITE);
        editorField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editorField.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180)));
        DefaultCellEditor editor = new DefaultCellEditor(editorField);
        styledTable.setDefaultEditor(Object.class, editor);

        return styledTable;
    }

    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);

        JLabel titleLabel = new JLabel("Visor/Editor de Configuración");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(0, 5, 0, 0));

        JButton closeButton = new JButton("X");
        styleTitleBarButton(closeButton);
        closeButton.addActionListener(e -> dispose());

        titleBar.add(titleLabel, BorderLayout.CENTER);
        titleBar.add(closeButton, BorderLayout.EAST);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { initialClick = e.getPoint(); }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                setLocation(thisX + xMoved, thisY + yMoved);
            }
        });
        return titleBar;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton saveButton = new JButton("Guardar Cambios");
        JButton closeButton = new JButton("Cerrar");

        styleFooterButton(saveButton);
        styleFooterButton(closeButton);

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        closeButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> saveChanges());

        return buttonPanel;
    }

    private void styleTitleBarButton(JButton button) {
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { button.setForeground(new Color(255, 100, 100)); }
            public void mouseExited(MouseEvent evt) { button.setForeground(Color.WHITE); }
        });
    }

    private void styleFooterButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(new Color(200, 220, 255));
        button.setBackground(new Color(255, 255, 255, 20));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 50)),
            new EmptyBorder(8, 15, 8, 15)
        ));
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { button.setBackground(new Color(255, 255, 255, 40)); }
            public void mouseExited(MouseEvent evt) { button.setBackground(new Color(255, 255, 255, 20)); }
        });
    }

    private void saveChanges() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(tableModel.getRowCount());
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            buf.writeString((String) tableModel.getValueAt(i, 0));
            buf.writeString((String) tableModel.getValueAt(i, 1));
        }
        ClientPlayNetworking.send(ModMessages.ACTUALIZAR_CONFIG_C2S, buf);
        JOptionPane.showMessageDialog(this, "Petición de guardado enviada al servidor.", "Guardar", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}

//estilo Aero
class AeroCellRenderer extends JTextArea implements TableCellRenderer {
    public AeroCellRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(false);
        setForeground(new Color(220, 220, 220));
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setBorder(new EmptyBorder(5, 8, 5, 8));
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((String) value);
        
        // Ajustar altura de la fila
        int fontHeight = getFontMetrics(getFont()).getHeight();
        int lines = getLineCount();
        int newHeight = (fontHeight * lines) + getInsets().top + getInsets().bottom;
        if (table.getRowHeight(row) != newHeight) {
            table.setRowHeight(row, newHeight);
        }

        // Color de fondo para la selección
        if (isSelected) {
            // fondo semitransparente detrás del JTextArea
            JPanel selectionPanel = new JPanel(new BorderLayout());
            selectionPanel.setBackground(new Color(70, 130, 180, 100));
            selectionPanel.add(this);
            return selectionPanel;
        }

        return this;
    }
}
