import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;

public class ReservationListPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel     cardPanel;
    private JTable     table;
    private JButton    topButton;

    public ReservationListPanel(CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel  = cardPanel;
        setLayout(new BorderLayout());

        String[] cols = { "Resv ID", "Member ID", "Costume ID", "Desired Start" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        loadData(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        topButton = new JButton("Top");
        topButton.addActionListener(e ->
            cardLayout.show(cardPanel, "EMPLOYEE_MENU"));
        JPanel south = new JPanel();
        south.add(topButton);
        add(south, BorderLayout.SOUTH);
    }

    private void loadData(DefaultTableModel model) {
        Path path = Paths.get("reservations.csv");
        if (!Files.exists(path)) return;
        try {
            for (String line : Files.readAllLines(path)) {
                String[] r = line.split(",", -1);
                // { resvId, memberId, costumeId, desired_start, ..., status }
                if (r.length >= 7 && "valid".equalsIgnoreCase(r[6])) {
                    model.addRow(new Object[]{ r[0], r[1], r[2], r[3] });
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Failed to load reservations.csv",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
