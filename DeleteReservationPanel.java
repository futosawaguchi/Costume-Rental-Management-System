// DeleteReservationPanel.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class DeleteReservationPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel     cardPanel;
    private int        memberId;

    private JTable     table;
    private JTextField resIdField;
    private JButton    deleteBtn, topBtn;

    public DeleteReservationPanel(CardLayout cardLayout, JPanel cardPanel, int memberId) {
        this.cardLayout = cardLayout;
        this.cardPanel  = cardPanel;
        this.memberId   = memberId;
        setLayout(new BorderLayout());

        table = new JTable();
        refreshTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.add(new JLabel("Reservation ID:"));
        resIdField = new JTextField(5);
        south.add(resIdField);

        deleteBtn = new JButton("Delete");
        topBtn    = new JButton("Top");
        south.add(deleteBtn);
        south.add(topBtn);

        add(south, BorderLayout.SOUTH);

        deleteBtn.addActionListener(e -> onDelete());
        topBtn   .addActionListener(e -> cardLayout.show(cardPanel, "MEMBER_MENU"));
    }

    private void refreshTable() {
        String[] cols = { "Resv ID", "Costume ID", "Size", "Color", "Desired Start" };
        List<String[]> rows = new ArrayList<>();

        Map<String, String[]> costumeMap = new HashMap<>();
        Path costumesPath = Paths.get("costume.csv");
        if (Files.exists(costumesPath)) {
            try {
                for (String line : Files.readAllLines(costumesPath)) {
                    String[] c = line.split(",", -1);
                    if (c.length >= 3) {
                        // c[0]=ID, c[1]=Size, c[2]=Color, …
                        costumeMap.put(c[0], new String[]{ c[1], c[2] });
                    }
                }
            } catch(IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Failed to load costume.csv",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Path resPath = Paths.get("reservations.csv");
        if (Files.exists(resPath)) {
            try {
                for (String line : Files.readAllLines(resPath)) {
                    String[] r = line.split(",", -1);
                    // r = { reservation_id, member_id, costume_id,
                    //       desired_start, actual_rent_date, actual_return, status }
                    if (r.length >= 7
                     && Integer.parseInt(r[1]) == memberId
                     && "valid".equalsIgnoreCase(r[6])) {

                        String resvId    = r[0];
                        String costumeId = r[2];
                        String start     = r[3];
                        String[] info    = costumeMap.getOrDefault(costumeId,
                                                new String[]{"",""});
                        String size      = info[0];
                        String color     = info[1];

                        rows.add(new String[]{
                            resvId, costumeId, size, color, start
                        });
                    }
                }
            } catch(IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Failed to load reservations.csv",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        String[][] data = rows.toArray(new String[0][]);
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table.setModel(model);
    }

    private void onDelete() {
        String idText = resIdField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter Reservation ID.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Path path = Paths.get("reservations.csv");
            List<String> all = Files.readAllLines(path);
            List<String> updated = new ArrayList<>();
            boolean done = false;

            for (String line : all) {
                String[] r = line.split(",", -1);
                if (!done
                 && r.length >= 7
                 && r[0].equals(idText)
                 && Integer.parseInt(r[1]) == memberId
                 && "valid".equalsIgnoreCase(r[6])) {
                    r[6] = "cancelled";
                    done = true;
                }
                updated.add(String.join(",", r));
            }

            if (!done) {
                JOptionPane.showMessageDialog(this,
                  "No matching valid reservation found.",
                  "Delete Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Files.write(path, updated,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            JOptionPane.showMessageDialog(this,
                "Reservation cancelled.",
                "Success", JOptionPane.INFORMATION_MESSAGE);

            resIdField.setText("");
            refreshTable();

        } catch(IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error updating reservations.csv",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
