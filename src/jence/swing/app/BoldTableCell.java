package jence.swing.app;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;

public class BoldTableCell {
    public static void main(String[] args) {
        // Sample data
        Object[][] data = {
                {"Row 1, Col 1", "Row 1, Col 2"},
                {"Row 2, Col 1", "Row 2, Col 2"}
        };
        String[] columnNames = {"Column 1", "Column 2"};

        // Create the table model
        DefaultTableModel model = new DefaultTableModel(data, columnNames);

        // Create the JTable with the model
        JTable table = new JTable(model);

        // Set up a custom cell renderer to render cells with bold font
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            Font boldFont = new Font(table.getFont().getName(), Font.BOLD, table.getFont().getSize());

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                // Call the super method to initialize the component
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Check if the cell should be bold
                if (row == 0 && column == 0) { // For example, make the cell at row 0 and column 0 bold
                    setFont(boldFont); // Set the font to bold
                } else {
                    setFont(table.getFont()); // Set the font to the table's default font
                }

                // Return the component
                return this;
            }
        };

        // Apply the custom cell renderer to the desired cell
        table.getColumnModel().getColumn(0).setCellRenderer(renderer); // For example, make the cell at column 0 bold in all rows

        // Create and display the frame
        JFrame frame = new JFrame("Bold TableCell Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(table));
        frame.pack();
        frame.setVisible(true);
    }
}
