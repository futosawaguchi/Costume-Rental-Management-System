// MyRentalsPanel.java
import javax.swing.*;
import java.awt.*;
import java.nio.file.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

/**
 * Panel to display the current member’s active rentals.
 */
public class MyRentalsPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel     cardPanel;
    private int        memberId;
    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_LOCAL_DATE;

    public MyRentalsPanel(CardLayout cardLayout, JPanel cardPanel, int memberId) {
        this.cardLayout = cardLayout;
        this.cardPanel  = cardPanel;
        this.memberId   = memberId;
        setLayout(new BorderLayout(10,10));

        // Title
        JLabel title = new JLabel("My Rentals", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // Load current rentals
        String[] cols = {"Costume ID","Rental Date","Due Date"};
        List<String[]> rows = loadActiveRentals();

        if (rows.isEmpty()) {
            // No active rentals
            JLabel none = new JLabel("You have no active rentals.", SwingConstants.CENTER);
            none.setFont(new Font("SansSerif", Font.ITALIC, 14));
            add(none, BorderLayout.CENTER);
        } else {
            // Show table
            String[][] data = rows.toArray(new String[0][]);
            JTable table = new JTable(data, cols);
            add(new JScrollPane(table), BorderLayout.CENTER);
        }

        // Back button
        JButton back = new JButton("Top");
        back.addActionListener(e -> cardLayout.show(cardPanel, "MEMBER_MENU"));
        JPanel south = new JPanel();
        south.add(back);
        add(south, BorderLayout.SOUTH);
    }

    private List<String[]> loadActiveRentals() {
        List<String[]> list = new ArrayList<>();
        Path path = Paths.get("rentals.csv");
        if (!Files.exists(path)) return list;

        try {
            for (String line : Files.readAllLines(path)) {
                // format: memberId,costumeId,rentalDate,dueDate,actualReturnDate,isLate,totalLateFee
                String[] f = line.split(",", -1);
                if (f.length < 5) continue;
                if (Integer.parseInt(f[0]) == memberId && (f[4].isEmpty() || f[4].equals("NULL"))) {
                    list.add(new String[]{ f[1], f[2], f[3] });
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Failed to load your rentals.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }
}
