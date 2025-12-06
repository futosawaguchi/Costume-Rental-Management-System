import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;

public class RentalHistoryPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel     cardPanel;
    private JTable     table;
    private JButton    topButton;

    public RentalHistoryPanel(CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel  = cardPanel;
        setLayout(new BorderLayout());

        String[] columnNames = {
            "Member ID",
            "Costume ID",
            "Rental Date",
            "Due Date",
            "Actual Return Date",
            "Late Fee"
        };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        Path path = Paths.get("rentals.csv");
        if (Files.exists(path)) {
            try {
                for (String line : Files.readAllLines(path)) {
                    String[] r = line.split(",", -1);
                    // r = { memberId, costumeId, rentalDate, dueDate,
                    //       actualReturnDate, isLate, totalLateFee }
                    if (r.length >= 7) {
                        model.addRow(new Object[]{
                            r[0],  
                            r[1],  
                            r[2],  
                            r[3],  
                            r[4],  
                            r[6]  
                        });
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Failed to load rentals.csv",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        topButton = new JButton("Top");
        topButton.addActionListener(e ->
            cardLayout.show(cardPanel, "EMPLOYEE_MENU"));
        JPanel south = new JPanel();
        south.add(topButton);
        add(south, BorderLayout.SOUTH);
    }
}
