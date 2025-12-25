package tontufosmp2.client.gui;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import tontufosmp2.Tontufosmp2Client;
import tontufosmp2.net.ModMessages;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class TiempoAdminWindow extends JFrame {

    private final DefaultTableModel tableModel;
    private Point initialClick;

    public TiempoAdminWindow(List<Tontufosmp2Client.PlayerData> playerDataList) {
        super("Panel de Administración de Tiempo");

        //Ventana sin bordes y transparente
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); // fondo transparente
        setSize(750, 500);
        setLocationRelativeTo(null);


        JPanel backgroundPanel = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // gradient
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 35, 40, 220), 0, getHeight(), new Color(50, 55, 65, 230));
                g2d.setPaint(gp);
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

                // borde
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));

                g2d.dispose();
            }
        };
        backgroundPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(backgroundPanel);

        // Barra titulo
        JPanel titleBar = createTitleBar();
        
        // Tabla

        String[] columnNames = {"Jugador", "Tiempo Jugado Hoy", "Tiempo Acumulado"};
        this.tableModel = new DefaultTableModel(columnNames, 0);


        JTable table = createStyledTable();
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false); // scroll pane transparente
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 50)));

        //botones
        JPanel buttonPanel = createButtonPanel();


        backgroundPanel.add(titleBar, BorderLayout.NORTH);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        //  logica
        updateData(playerDataList);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                Tontufosmp2Client.onAdminWindowClosed();
            }
        });
    }
    
    private JTable createStyledTable() {
        JTable table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (c instanceof JComponent) {
                    ((JComponent) c).setOpaque(false); // Celdas transparentes
                }
                c.setForeground(new Color(220, 220, 220));
                if (isRowSelected(row)) {
                    c.setBackground(new Color(70, 130, 180, 100)); // Selección semitransparente
                }
                return c;
            }
        };

        table.setOpaque(false);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setGridColor(new Color(255, 255, 255, 50));
        table.setForeground(new Color(220, 220, 220));

        JTableHeader header = table.getTableHeader();
        header.setOpaque(false);
        header.setBackground(new Color(0, 0, 0, 80));
        header.setForeground(new Color(255, 255, 255));
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 50)));

        return table;
    }

    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);

        JLabel titleLabel = new JLabel("Panel de Administración de Tiempo");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(0, 5, 0, 0));

        JButton closeButton = new JButton("X");
        styleTitleBarButton(closeButton);
        closeButton.addActionListener(e -> dispose());

        titleBar.add(titleLabel, BorderLayout.CENTER);
        titleBar.add(closeButton, BorderLayout.EAST);

        // permite mover la ventana
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                setLocation(X, Y);
            }
        });

        return titleBar;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton viewConfigButton = new JButton("Ver/Editar Config");
        JButton resetDayButton = new JButton("Simular Reset Diario");
        JButton refreshButton = new JButton("Refrescar Datos");
        
        styleFooterButton(viewConfigButton);
        styleFooterButton(resetDayButton);
        styleFooterButton(refreshButton);

        buttonPanel.add(refreshButton);
        buttonPanel.add(viewConfigButton);
        buttonPanel.add(resetDayButton);

        viewConfigButton.addActionListener(e -> ClientPlayNetworking.send(ModMessages.PEDIR_CONFIG_C2S, PacketByteBufs.create())); // CORREGIDO
        resetDayButton.addActionListener(e -> sendCommand("test reset_day"));
        refreshButton.addActionListener(e -> MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().getNetworkHandler().sendChatCommand("tiempo test gui")));

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
            public void mouseEntered(MouseEvent evt) {
                button.setForeground(new Color(255, 100, 100));
            }
            public void mouseExited(MouseEvent evt) {
                button.setForeground(Color.WHITE);
            }
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
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(255, 255, 255, 40));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(255, 255, 255, 20));
            }
        });
    }

    public void updateData(List<Tontufosmp2Client.PlayerData> playerDataList) {
        tableModel.setRowCount(0);
        for (Tontufosmp2Client.PlayerData data : playerDataList) {
            tableModel.addRow(new Object[]{data.name(), data.timePlayed(), data.timeAccumulated()});
        }
    }

    private void sendCommand(String command) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(command);
        ClientPlayNetworking.send(ModMessages.EJECUTAR_COMANDO_C2S, buf); // CORREGIDO
    }
}
