// DeleteCostumePanel.java
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class DeleteCostumePanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel     cardPanel;
    private DefaultTableModel model;
    private JTable     table;
    private Set<String> lockedIds = new HashSet<>();

    public DeleteCostumePanel(CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel  = cardPanel;
        setLayout(new BorderLayout());

        String[] cols = {"ID","Size","Color","Price","LateFee","Period","Status"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model) {
            @Override public Component prepareRenderer(
                    TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                String id = getValueAt(row, 0).toString();
                if (lockedIds.contains(id)) {
                    c.setBackground(new Color(0xFFCCCC));
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        };
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.add(new JLabel("Costume ID to delete:"));
        JTextField idField = new JTextField(5);
        south.add(idField);

        JButton delBtn = new JButton("Delete");
        JButton topBtn = new JButton("Top");
        south.add(delBtn);
        south.add(topBtn);
        add(south, BorderLayout.SOUTH);

        reloadModel();

        delBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter an ID.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // if (lockedIds.contains(id)) {
            //     JOptionPane.showMessageDialog(this,
            //         "Cannot delete. Costume ID " + id + " is currently rented out.",
            //         "Delete Error", JOptionPane.ERROR_MESSAGE);
            //     return;
            // }
            // int ans = JOptionPane.showConfirmDialog(this,
            //     "Are you sure you want to delete costume ID " + id + "?",
            //     "Confirm Delete", JOptionPane.YES_NO_OPTION,
            //     JOptionPane.QUESTION_MESSAGE);
            // if (ans != JOptionPane.YES_OPTION) return;
            if (lockedIds.contains(id)) {
                JOptionPane.showMessageDialog(this,
                    "Cannot delete. Costume ID " + id + " is currently rented or reserved.",
                    "Delete Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Object[] options = {"Yes", "No"};
            int ans = JOptionPane.showOptionDialog(this,
                "Are you sure you want to delete costume ID " + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
            if (ans != 0) return;            

            try {
                List<String> lines = Files.readAllLines(Paths.get("costume.csv"));
                List<String> out = new ArrayList<>();
                for (String line : lines) {
                    if (line.startsWith(id + ",")) {
                        out.add("");
                    } else {
                        out.add(line);
                    }
                }
                Files.write(
                  Paths.get("costume.csv"),
                  out,
                  StandardOpenOption.CREATE,
                  StandardOpenOption.TRUNCATE_EXISTING
                );
                JOptionPane.showMessageDialog(this,
                    "Costume deleted.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                reloadModel();
                idField.setText("");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Failed to delete.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        topBtn.addActionListener(e ->
            cardLayout.show(cardPanel, "EMPLOYEE_MENU"));
    }

    private void reloadModel() {
        lockedIds.clear();
        Path rentals = Paths.get("rentals.csv");
        if (Files.exists(rentals)) {
            try {
                for (String line : Files.readAllLines(rentals)) {
                    String[] r = line.split(",", -1);
                    // { memberId, costumeId, rentDate, dueDate, actReturn, ... }
                    if (r.length >= 5 && (r[4].isEmpty())) {
                        lockedIds.add(r[1]);
                    }
                }
            } catch (IOException ignored) { }
        }

        Path res = Paths.get("reservations.csv");
        if (Files.exists(res)) {
            try {
                for (String line : Files.readAllLines(res)) {
                    String[] r = line.split(",", -1);
                    if (r.length >= 7 && "valid".equalsIgnoreCase(r[6])) {
                        lockedIds.add(r[2]);  // costume_id = r[2]
                    }
                }
            } catch (IOException ignored) {}
        }        

        model.setRowCount(0);
        Path path = Paths.get("costume.csv");
        if (!Files.exists(path)) return;
        try {
            for (String line : Files.readAllLines(path)) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",", -1);
                model.addRow(p);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Failed to load costume.csv",
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
