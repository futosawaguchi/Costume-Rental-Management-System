// ReservationActionPanel.java
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReservationActionPanel extends JPanel {
    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_LOCAL_DATE;

    private CardLayout cardLayout;
    private JPanel     cardPanel;
    private int        memberId;

    private JTextField costumeIdField;
    private JComboBox<String> startDateCombo;
    private JButton    reserveButton;
    private JButton    topButton;

    public ReservationActionPanel(CardLayout cardLayout, JPanel cardPanel, int memberId) {
        this.cardLayout = cardLayout;
        this.cardPanel  = cardPanel;
        this.memberId   = memberId;

        setLayout(new BorderLayout());
        JPanel north = new JPanel(new GridLayout(2,1));
        JLabel title = new JLabel("Make Reservation", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        north.add(title);
        JLabel hint = new JLabel("After typing Costume ID, please press ENTER", SwingConstants.CENTER);
        hint.setFont(new Font("SansSerif", Font.ITALIC, 12));
        north.add(hint);
        add(north, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,10,8,10);
        gbc.anchor = GridBagConstraints.WEST;
        int y = 0;

        // Costume ID
        gbc.gridx=0; gbc.gridy=y;
        form.add(new JLabel("Costume ID:"), gbc);
        costumeIdField = new JTextField(6);
        gbc.gridx=1;
        form.add(costumeIdField, gbc);

        // Desired Start
        y++; gbc.gridx=0; gbc.gridy=y;
        form.add(new JLabel("Desired Start:"), gbc);
        startDateCombo = new JComboBox<>();
        gbc.gridx=1;
        form.add(startDateCombo, gbc);

        add(form, BorderLayout.CENTER);

        JPanel btnPane = new JPanel();
        reserveButton = new JButton("Reserve");
        topButton     = new JButton("Top");
        btnPane.add(reserveButton);
        btnPane.add(topButton);
        add(btnPane, BorderLayout.SOUTH);

        costumeIdField.addActionListener(e -> populateStartDates());

        reserveButton.addActionListener(e -> onReserve());
        topButton    .addActionListener(e -> {
            clearFields();
            cardLayout.show(cardPanel, "MEMBER_MENU");
        });
    }

    private void populateStartDates() {
        startDateCombo.removeAllItems();
        String idText = costumeIdField.getText().trim();
        if (idText.isEmpty()) return;

        Path resPath = Paths.get("reservations.csv");
        if (Files.exists(resPath)) {
            try {
                for (String line : Files.readAllLines(resPath)) {
                    String[] r = line.split(",", -1);
                    // r = { reservation_id, member_id, costume_id, desired_start,
                    //       actual_rent_date, actual_return, status }
                    if (r.length >= 7
                     && r[2].equals(idText)
                     && "valid".equalsIgnoreCase(r[6])) {
                        JOptionPane.showMessageDialog(this,
                            "Costume ID " + idText + " is already reserved.",
                            "Reservation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Failed to read reservations.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        LocalDate dueDate = null;
        try {
            Path rentals = Paths.get("rentals.csv");
            if (Files.exists(rentals)) {
                for (String line : Files.readAllLines(rentals)) {
                    String[] r = line.split(",", -1);
                    // { memberId, costumeId, rentalDate, dueDate, actualReturn, isLate, totalLateFee }
                    if (r.length >= 5
                     && r[1].equals(idText)
                     && r[4].isEmpty()) {
                        dueDate = LocalDate.parse(r[3], DTF);
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Failed to load rental information.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (dueDate == null) {
            JOptionPane.showMessageDialog(this,
                "No active rental found for Costume ID " + idText,
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate start = dueDate.plusDays(1);
        for (int i = 0; i < 10; i++) {
            startDateCombo.addItem(start.plusDays(i).format(DTF));
        }
    }

    private void onReserve() {
        String idText = costumeIdField.getText().trim();
        String start  = (String)startDateCombo.getSelectedItem();
        if (idText.isEmpty() || start == null) {
            JOptionPane.showMessageDialog(this,
                "Both Costume ID and Desired Start are required.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Path path = Paths.get("reservations.csv");
            int nextId = 1;
            if (Files.exists(path)) {
                nextId = (int)Files.lines(path).count() + 1;
            }

            String line = String.join(",",
                String.valueOf(nextId),
                String.valueOf(memberId),
                idText,
                start,
                "",    // actual_rent_date
                "",    // actual_return
                "valid"
            );
            Files.write(path,
                (line + System.lineSeparator()).getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );

            JOptionPane.showMessageDialog(this,
                "Reservation confirmed!",
                "Success", JOptionPane.INFORMATION_MESSAGE);

            clearFields();
            cardLayout.show(cardPanel, "MEMBER_MENU");

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Failed to save reservation.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        costumeIdField.setText("");
        startDateCombo.removeAllItems();
    }
}
