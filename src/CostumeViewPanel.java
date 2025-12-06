// CostumeViewPanel.java
import javax.swing.*;
import java.awt.*;
import java.nio.file.*;
import java.io.IOException;
import java.util.List;

public class CostumeViewPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private String    returnCard;

    // public CostumeViewPanel(CardLayout cardLayout, JPanel cardPanel) {
    //     this.cardLayout = cardLayout;
    //     this.cardPanel  = cardPanel;
    public CostumeViewPanel(CardLayout cardLayout, JPanel cardPanel, String returnCard) {
        this.cardLayout  = cardLayout;
        this.cardPanel   = cardPanel;
        this.returnCard  = returnCard;
        setLayout(new BorderLayout());

        String[] columnNames = {
            "Costume ID", "Size", "Color", "Price", "Late fee per day", "Period (days)", "Status"
        };
        List<String[]> rows = new java.util.ArrayList<>();

        try {
            Path path = Paths.get("costume.csv");
            if (Files.exists(path)) {
                for (String line : Files.readAllLines(path)) {
                    String[] parts = line.split(",", -1);
                    if (parts.length == 7) {
                        rows.add(parts);
                    }
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Failed to load costume data.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        String[][] data = rows.toArray(new String[0][]);
        JTable table = new JTable(data, columnNames);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton topButton = new JButton("Top");
        topButton.addActionListener(e ->
            cardLayout.show(cardPanel, returnCard) 
        );
        JPanel south = new JPanel();
        south.add(topButton);
        add(south, BorderLayout.SOUTH);
    }
}
