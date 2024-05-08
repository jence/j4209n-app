package jence.swing.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class HorizontalScrollableTableExample extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public HorizontalScrollableTableExample() {
        setTitle("Horizontal Scrollable Table Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        // Create sample data
        Object[][] data = new Object[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                data[i][j] = "Cell " + i + ", " + j;
            }
        }

        // Create column names
        String[] columnNames = new String[10];
        for (int i = 0; i < 10; i++) {
            columnNames[i] = "Column " + i;
        }

        // Create table model with sample data
        model = new DefaultTableModel(data, columnNames);
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Disable auto-resizing

        // Calculate preferred width for the table
        int preferredWidth = table.getColumnModel().getTotalColumnWidth();

        // Set horizontal scroll policy for the table's scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(preferredWidth, 200)); // Set preferred size for the scroll pane

        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HorizontalScrollableTableExample example = new HorizontalScrollableTableExample();
            example.setVisible(true);
        });
    }
}
