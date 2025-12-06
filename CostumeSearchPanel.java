// CostumeSearchPanel.java
import javax.swing.*;
import java.awt.*;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class CostumeSearchPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private String    returnCard;

    private JTextField sizeField, colorField, priceField, feeField;
    private JTextField periodField;
    private JComboBox<String> statusCombo;

    public CostumeSearchPanel(CardLayout cardLayout, JPanel cardPanel, String returnCard) {
        this.cardLayout = cardLayout;
        this.cardPanel  = cardPanel;
        this.returnCard = returnCard;
        setLayout(new BorderLayout());

        JPanel inputPane = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,8,4,8);
        gbc.anchor = GridBagConstraints.WEST;
        int y = 0;

        gbc.gridx=0; gbc.gridy=y;
        inputPane.add(new JLabel("Size:"), gbc);
        sizeField = new JTextField(10);
        gbc.gridx=1;
        inputPane.add(sizeField, gbc);

        y++; gbc.gridx=0; gbc.gridy=y;
        inputPane.add(new JLabel("Color:"), gbc);
        colorField = new JTextField(10);
        gbc.gridx=1;
        inputPane.add(colorField, gbc);

        y++; gbc.gridx=0; gbc.gridy=y;
        inputPane.add(new JLabel("Max Price:"), gbc);
        priceField = new JTextField(8);
        gbc.gridx=1;
        inputPane.add(priceField, gbc);

        y++; gbc.gridx=0; gbc.gridy=y;
        inputPane.add(new JLabel("Max Late Fee:"), gbc);
        feeField = new JTextField(8);
        gbc.gridx=1;
        inputPane.add(feeField, gbc);

        y++; gbc.gridx=0; gbc.gridy=y;
        inputPane.add(new JLabel("Max Period (days):"), gbc);
        periodField = new JTextField(8);
        gbc.gridx=1;
        inputPane.add(periodField, gbc);

        y++; gbc.gridx=0; gbc.gridy=y;
        inputPane.add(new JLabel("Status:"), gbc);
        statusCombo = new JComboBox<>(new String[]{"", "Available", "Rented"});
        gbc.gridx=1;
        inputPane.add(statusCombo, gbc);

        JPanel btnPane = new JPanel();
        JButton doSearch = new JButton("Search Costumes");
        JButton back     = new JButton("Top");
        btnPane.add(doSearch);
        btnPane.add(back);

        doSearch.addActionListener(e -> performSearch());
        back.addActionListener(e -> {
            clearFields();
            cardLayout.show(cardPanel, returnCard); 
        });

        add(inputPane, BorderLayout.CENTER);
        add(btnPane,   BorderLayout.SOUTH);
    }

    private void performSearch() {
        String size      = sizeField.getText().trim();
        String color     = colorField.getText().trim();
        String pTxt      = priceField.getText().trim();
        String fTxt      = feeField.getText().trim();
        String periodTxt = periodField.getText().trim();
        String status    = (String)statusCombo.getSelectedItem();

        if (size.isEmpty()
        && color.isEmpty()
        && pTxt.isEmpty()
        && fTxt.isEmpty()
        && periodTxt.isEmpty()
        && (status == null || status.isEmpty())
        ) {
            JOptionPane.showMessageDialog(this,
                "Please specify at least one criterion.",
                "Input Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }        

        List<String[]> results = new ArrayList<>();
        try {
            Path path = Paths.get("costume.csv");
            if (Files.exists(path)) for (String line : Files.readAllLines(path)) {
                String[] c = line.split(",", -1);
                if (c.length != 7) continue;
                boolean ok = true;
                if (!size.isEmpty() && !c[1].equalsIgnoreCase(size)) ok=false;
                if (!color.isEmpty()&& !c[2].equalsIgnoreCase(color))ok=false;
                if (!pTxt.isEmpty()) {
                    try {
                        if (Double.parseDouble(c[3])>Double.parseDouble(pTxt)) ok=false;
                    } catch(NumberFormatException ex) {ok=false;}
                }
                if (!fTxt.isEmpty()) {
                    try {
                        if (Double.parseDouble(c[4])>Double.parseDouble(fTxt)) ok=false;
                    } catch(NumberFormatException ex) {ok=false;}
                }
                String perTxt = periodField.getText().trim();
                if (!perTxt.isEmpty()) {
                    try {
                        int maxPeriod = Integer.parseInt(perTxt);
                        int period    = Integer.parseInt(c[5]);
                        if (period > maxPeriod) ok = false;
                    } catch (NumberFormatException ex) {
                        ok = false;
                    }
                }
                if (status!=null && !status.isEmpty() && !c[6].equalsIgnoreCase(status)) ok=false;
                if (ok) results.add(c);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Failed to read costume.csv",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No matching costumes found.",
                "No Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String[] cols = {
              "ID","Size","Color","Price",
              "Late fee/day","Period(days)","Status"
            };
            String[][] data = results.toArray(new String[0][]);
            JFrame f = new JFrame("Search Results");
            f.getContentPane().add(new JScrollPane(new JTable(data, cols)));
            f.setSize(600,300);
            f.setLocationRelativeTo(this);
            f.setVisible(true);
        }

        clearFields();
    }

    private void clearFields() {
        sizeField.setText("");
        colorField.setText("");
        priceField.setText("");
        feeField.setText("");
        periodField.setText("");
        statusCombo.setSelectedIndex(0);
    }
}
