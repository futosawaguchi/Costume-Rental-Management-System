import javax.swing.*;
import java.awt.*;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.swing.Box;
import javax.swing.table.DefaultTableCellRenderer;

public class ReservationPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel     cardPanel;
    private int        memberId;

    public ReservationPanel(CardLayout cardLayout, JPanel cardPanel, int memberId) {
        this.cardLayout = cardLayout;
        this.cardPanel  = cardPanel;
        this.memberId   = memberId;
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Reservation", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        String[] cols = {
            "Costume ID",
            "Due Date",
            "Size",
            "Color",
            "Period(days)"
        };

        List<String[]> rows = loadRentedCostumesWithDueDates();
        if (rows.isEmpty()) {
            add(new JLabel("No costumes available for reservation.", SwingConstants.CENTER),
                BorderLayout.CENTER);
        } else {
            String[][] data = rows.toArray(new String[0][]);
            JTable table = new JTable(data, cols);
            Set<String> reservedIds = loadReservedCostumeIds();
            table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable tbl, Object value,
                        boolean isSelected, boolean hasFocus, int row, int col) {
                    Component c = super.getTableCellRendererComponent(
                        tbl, value, isSelected, hasFocus, row, col);
                    String costumeId = (String)tbl.getValueAt(row, 0);
                    if (reservedIds.contains(costumeId)) {
                        c.setBackground(Color.PINK);
                    } else {
                        c.setBackground(isSelected
                            ? tbl.getSelectionBackground()
                            : tbl.getBackground());
                    }
                    return c;
                }
            });
            add(new JScrollPane(table), BorderLayout.CENTER);
        }

        JPanel btnPane = new JPanel();
        JButton reserveBtn = new JButton("Reservation costume");
        JButton deleteBtn  = new JButton("Delete reservation");
        JButton topBtn     = new JButton("Top");
        btnPane.add(reserveBtn);
        btnPane.add(deleteBtn);
        btnPane.add(topBtn);
        add(btnPane, BorderLayout.SOUTH);

        reserveBtn.addActionListener(e -> {
            ReservationActionPanel rap =
                new ReservationActionPanel(cardLayout, cardPanel, memberId);
            cardPanel.add(rap, "MAKE_RESERVATION");
            cardLayout.show(cardPanel, "MAKE_RESERVATION");
        });


        deleteBtn.addActionListener(e -> {
            DeleteReservationPanel drp =
                new DeleteReservationPanel(cardLayout, cardPanel, memberId);
            cardPanel.add(drp, "DELETE_RESERVATION");
            cardLayout.show(cardPanel, "DELETE_RESERVATION");
        });
        topBtn.addActionListener(e ->
            cardLayout.show(cardPanel, "MEMBER_MENU")
        );
    }


    private List<String[]> loadRentedCostumesWithDueDates() {
        Map<String,String> dueDateMap = new HashMap<>();
        Path rentalsPath = Paths.get("rentals.csv");
        if (Files.exists(rentalsPath)) {
            try {
                for (String line : Files.readAllLines(rentalsPath)) {
                    String[] r = line.split(",", -1);
                    // r = { memberId, costumeId, rentalDate, dueDate, actualReturnDate, isLate, totalLateFee }
                    if (r.length >= 5
                     && Integer.parseInt(r[0]) != memberId
                     && (r[4].isEmpty() || "NULL".equals(r[4]))) {
                        dueDateMap.put(r[1], r[3]);
                    }
                }
            } catch (IOException ignored) { /*null */ }
        }

        List<String[]> list = new ArrayList<>();
        Path costumesPath = Paths.get("costume.csv");
        if (Files.exists(costumesPath)) {
            try {
                for (String line : Files.readAllLines(costumesPath)) {
                    String[] c = line.split(",", -1);
                    // c = { id, size, color, price, lateFee, period, status }
                    if (c.length == 7
                     && "Rented".equalsIgnoreCase(c[6])
                     && dueDateMap.containsKey(c[0])) {
                        // { id, dueDate, size, color, period }
                        list.add(new String[]{
                            c[0],
                            dueDateMap.get(c[0]),
                            c[1],
                            c[2],
                            c[5]
                        });
                    }
                }
            } catch (IOException ignored) { }
        }
        return list;
    }
    private Set<String> loadReservedCostumeIds() {
            Set<String> reserved = new HashSet<>();
            Path path = Paths.get("reservations.csv");
            if (!Files.exists(path)) return reserved;
            try {
                for (String line : Files.readAllLines(path)) {
                    String[] r = line.split(",", -1);
                    // reservation_id, member_id, costume_id, desired_start,
                    // actual_rent_date, actual_return, status
                    if (r.length >= 7 && "valid".equalsIgnoreCase(r[6])) {
                        reserved.add(r[2]);
                    }
                }
            } catch (IOException e) {
            }
            return reserved;
        }    
}


