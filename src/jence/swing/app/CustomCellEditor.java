package jence.swing.app;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

public class CustomCellEditor extends AbstractCellEditor implements TableCellEditor {
    private JTextField textField;
    private Object originalValue; // Store original value
    private JTable table;
    private int row, col;
    private int maxCharacters;
    private String allowedCharacters;

    public CustomCellEditor(JTable table, int maxCharacters, String allowedCharacters) {
        this.table = table;
        this.maxCharacters = maxCharacters;
        this.allowedCharacters = allowedCharacters;

        textField = new JTextField();
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopCellEditing();  // Stop editing when Enter key is pressed
            }
        });

        // Apply document filter for limited characters and uppercase conversion
        textField.setDocument(new LimitedDocument(maxCharacters, allowedCharacters));
    }


    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        originalValue = value; // Store original value
        textField.setText(value != null ? value.toString() : ""); // Set cell content for editing
        textField.selectAll(); // Select all text for quick replacement
        this.row = row;
        this.col = column;
        return textField;
    }

    @Override
    public Object getCellEditorValue() {
        return textField.getText(); // Return edited value
    }

    @Override
    public boolean stopCellEditing() {
        String editedValue = textField.getText();
        if (editedValue.isEmpty()) {
            cancelCellEditing(); // Cancel editing if value is empty
            return true;
        } else if (!editedValue.equals(originalValue)) {
            // Apply editing if value has changed
            setValueAt(editedValue, row, col);
            // Optionally, handle specific behavior for changed values
        }
        return super.stopCellEditing();
    }

    @Override
    public void cancelCellEditing() {
        // Restore original value when editing is canceled
        setValueAt(originalValue, row, col);
        super.cancelCellEditing();
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        return true; // Allow cell editing
    }

    @Override
    public boolean shouldSelectCell(EventObject e) {
        return true; // Allow cell selection
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        // Optional: Implement if you need to track editor events
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        // Optional: Implement if you need to track editor events
    }

    private void setValueAt(Object value, int row, int column) {
        table.getModel().setValueAt(value, row, column);
    }

    class LimitedDocument extends PlainDocument {
        private int maxCharacters;
        private String allowedCharacters;

        public LimitedDocument(int maxCharacters, String allowedCharacters) {
            this.maxCharacters = maxCharacters;
            this.allowedCharacters = allowedCharacters;
        }

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str == null) {
                return;
            }

            // Check if the resulting text would be within the character limit
            if (getLength() + str.length() <= maxCharacters) {
                // Check if all characters in 'str' are allowed
                for (char c : str.toCharArray()) {
                    if (allowedCharacters.indexOf(Character.toUpperCase(c)) == -1) {
                        return; // Disallow insertion if any character is not allowed
                    }
                }
                super.insertString(offs, str.toUpperCase(), a); // Convert to uppercase
            }
        }
    }
}
