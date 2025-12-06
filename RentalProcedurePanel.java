// RentalProcedurePanel.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

/**
 * Panel for members to rent costumes, honoring any existing reservation.
 */
public class RentalProcedurePanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel     cardPanel;
    private String     returnCard;
    private int        memberId;

    private JTable     table;
    private JTextField idField;
    private JTextField sizeField;

    public RentalProcedurePanel(CardLayout cardLayout,
                                JPanel cardPanel,
                                int memberId,
                                String returnCard) {
        this.cardLayout = cardLayout;
        this.cardPanel  = cardPanel;
        this.memberId   = memberId;
        this.returnCard = returnCard;

        setLayout(new BorderLayout());

        table = new JTable();
        refreshTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.add(new JLabel("Costume ID:"));
        idField = new JTextField(5);
        south.add(idField);

        south.add(new JLabel("Size:"));
        sizeField = new JTextField(3);
        south.add(sizeField);

        JButton rentBtn = new JButton("Rent");
        JButton topBtn  = new JButton("Top");
        south.add(rentBtn);
        south.add(topBtn);
        add(south, BorderLayout.SOUTH);

        rentBtn.addActionListener(e -> performRent());
        topBtn .addActionListener(e -> cardLayout.show(cardPanel, returnCard));
    }

    private void refreshTable() {
        String[] columnNames = {
            "ID","Size","Color","Price","LateFee/day","Period","Status"
        };
        List<String[]> rows = new ArrayList<>();
        try {
            Path path = Paths.get("costume.csv");
            if (Files.exists(path)) {
                for (String line : Files.readAllLines(path)) {
                    String[] p = line.split(",", -1);
                    if (p.length == 7) rows.add(p);
                }
            }
        } catch(IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Failed to load costume.csv",
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        String[][] data = rows.toArray(new String[0][]);
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table.setModel(model);
    }

    private void performRent() {
        String idTxt   = idField.getText().trim();
        String sizeTxt = sizeField.getText().trim();
        if (idTxt.isEmpty() || sizeTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Both Costume ID and Size are required.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Path path = Paths.get("costume.csv");
            List<String[]> all = new ArrayList<>();
            boolean done = false;
            int    price = 0;
            String status = null;
            for (String line : Files.readAllLines(path)) {
                String[] p = line.split(",", -1);
                if (!done
                 && p[0].equals(idTxt)
                 && p[1].equalsIgnoreCase(sizeTxt)
                 && p[6].equals("Available")) {
                    price = Integer.parseInt(p[3]);
                    p[6] = "Rented";
                    status = "Rented";
                    done = true;
                }
                all.add(p);
            }
            if (!done) {
                JOptionPane.showMessageDialog(this,
                    "This costume is not available: it may be rented out or the details may be incorrect.",
                    "Rent Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Path resPath = Paths.get("reservations.csv");
            List<String> resLines = Files.exists(resPath)
                ? Files.readAllLines(resPath)
                : Collections.emptyList();
            boolean reservedByOther = false;
            boolean reservedByMe    = false;

            for (String line : resLines) {
                String[] r = line.split(",", -1);
                // r = { reservation_id, member_id, costume_id, desired_start,
                //       actual_rent_date, actual_return, status }
                if (r.length>=7 && r[2].equals(idTxt) && "valid".equalsIgnoreCase(r[6])) {
                    int owner = Integer.parseInt(r[1]);
                    if (owner == memberId) {
                        reservedByMe = true;
                    } else {
                        reservedByOther = true;
                        break;
                    }
                }
            }
            if (reservedByOther) {
                JOptionPane.showMessageDialog(this,
                    "This costume is already reserved by another member.",
                    "Rent Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<String> out = new ArrayList<>();
            for (String[] p : all) out.add(String.join(",", p));
            Files.write(path, out, StandardOpenOption.TRUNCATE_EXISTING);

            LocalDate today = LocalDate.now();
            int period = Integer.parseInt(
                all.stream().filter(p -> p[0].equals(idTxt)).findFirst().get()[5]
            );
            LocalDate due = today.plusDays(period);
            String rentLine = memberId + "," + idTxt + "," + today + "," + due + ",,,";
            Files.write(Paths.get("rentals.csv"),
                        Collections.singleton(rentLine),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);

            if (reservedByMe) {
                List<String> updated = new ArrayList<>();
                for (String line : resLines) {
                    String[] r = line.split(",", -1);
                    if (r.length>=7
                     && r[2].equals(idTxt)
                     && Integer.parseInt(r[1])==memberId
                     && "valid".equalsIgnoreCase(r[6])) {
                        r[4] = today.toString();  // actual_rent_date
                        r[6] = "completed";       // status
                    }
                    updated.add(String.join(",", r));
                }
                Files.write(resPath,
                            updated,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING);
            }

            JOptionPane.showMessageDialog(this,
                String.format("Rental completed!\nDue date: %s\nPrice: ¥%d",
                              due, price),
                "Success", JOptionPane.INFORMATION_MESSAGE);

            idField.setText("");
            sizeField.setText("");
            refreshTable();

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error during rental process.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
