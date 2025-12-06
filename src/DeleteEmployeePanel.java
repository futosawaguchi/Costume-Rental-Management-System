// DeleteEmployeePanel.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeleteEmployeePanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel     cardPanel;
    private DefaultTableModel model;
    private JTable            table;
    private JTextField        idField;

    public DeleteEmployeePanel(CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel  = cardPanel;
        setLayout(new BorderLayout());

        String[] cols = {"Employee ID","Name","Email","RegDate"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        reloadModel();

        JPanel south = new JPanel();
        south.add(new JLabel("Employee ID to delete:"));
        idField = new JTextField(5);
        south.add(idField);
        JButton delBtn = new JButton("Delete");
        JButton topBtn = new JButton("Top");
        south.add(delBtn);
        south.add(topBtn);
        add(south, BorderLayout.SOUTH);

        delBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter an ID.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            Object[] options = {"Yes","No"};
            int choice = JOptionPane.showOptionDialog(
                this,
                "Are you sure you want to delete employee ID " + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]
            );
            if (choice != 0) return;

            try {
                Path path = Paths.get("employees.csv");
                List<String> lines = Files.readAllLines(path);
                List<String> updated = new ArrayList<>();
                for (String line : lines) {
                    if (line.startsWith(id + ",")) {
                        updated.add("");
                    } else {
                        updated.add(line);
                    }
                }
                Files.write(path,
                    updated,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

                JOptionPane.showMessageDialog(this,
                    "Employee deleted.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

                reloadModel();
                idField.setText("");

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Failed to delete.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        topBtn.addActionListener(e -> {
            idField.setText("");
            reloadModel();
            cardLayout.show(cardPanel, "EMPLOYEE_MENU");
        });
    }

    private void reloadModel() {
        model.setRowCount(0);
        Path path = Paths.get("employees.csv");
        if (!Files.exists(path)) return;
        try {
            for (String line : Files.readAllLines(path)) {
                if (line.trim().isEmpty()) continue;
                model.addRow(line.split(",", -1));
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Failed to load employees.csv",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
