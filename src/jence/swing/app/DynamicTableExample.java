package jence.swing.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.ArrayList;

public class DynamicTableExample extends JFrame {
    private JTable table;
    private JScrollPane scrollPane;
    private ArrayList<String> columnNames;
    private ArrayList<ArrayList<Object>> data;

    public DynamicTableExample() {
        setTitle("Dynamic Table Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        columnNames = new ArrayList<>();
        data = new ArrayList<>();

        // Create the initial table with empty data
        table = new JTable(new DefaultTableModel());
        scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Add a button to dynamically add a new column
        JButton addButton = new JButton("Add Column");
        addButton.addActionListener(e -> addColumn());
        getContentPane().add(addButton, BorderLayout.SOUTH);
    }

    private void addColumn() {
        // Prompt user for the column name
        String columnName = JOptionPane.showInputDialog("Enter Column Name:");

        // Add the column name to the list of column names
        columnNames.add(columnName);

        // Create a new column array for each row of data
        for (ArrayList<Object> row : data) {
            row.add("df"); // Add an empty value for the new column
        }

        // Update the table model with the new column names and data
        DefaultTableModel model = new DefaultTableModel();
        for (String name : columnNames) {
            model.addColumn(name);
        }
        for (ArrayList<Object> row : data) {
            model.addRow(row.toArray());
        }
        table.setModel(model);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DynamicTableExample example = new DynamicTableExample();
            example.setVisible(true);
        });
    }
}
