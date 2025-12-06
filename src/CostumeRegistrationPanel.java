import javax.swing.*;
import java.awt.*;
import java.nio.file.*;
import java.io.IOException;
// import java.time.LocalDate;
import javax.swing.Box;

public class CostumeRegistrationPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public CostumeRegistrationPanel(CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel  = cardPanel;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel sizeLabel = new JLabel("Size:");
        JTextField sizeField = new JTextField(10);
        sizeField.setMaximumSize(new Dimension(300, 25));

        JLabel colorLabel = new JLabel("Color:");
        JTextField colorField = new JTextField(10);
        colorField.setMaximumSize(new Dimension(300, 25));

        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField(10);
        priceField.setMaximumSize(new Dimension(300, 25));

        JLabel feeLabel = new JLabel("Late fee per day:");
        JTextField feeField = new JTextField(10);
        feeField.setMaximumSize(new Dimension(300, 25));

        JLabel periodLabel = new JLabel("Available Period (days):");
        JTextField periodField = new JTextField(10);
        periodField.setMaximumSize(new Dimension(300, 25));

        JButton registerButton = new JButton("Register Costume");
        JButton topButton      = new JButton("Top");

        registerButton.addActionListener(e -> {
            String size   = sizeField.getText().trim();
            String color  = colorField.getText().trim();
            String price  = priceField.getText().trim();
            String period = periodField.getText().trim();
            if (size.isEmpty() || color.isEmpty() || price.isEmpty() || period.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "All fields are required.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int newId = getNextCostumeId();
                String lateFee = feeField.getText().trim();
                String status = "Available";
                String line = newId + "," + size + "," + color + "," +
                              price + "," + lateFee + "," + period + "," + status;

                Files.write(Paths.get("costume.csv"),
                            (line + System.lineSeparator()).getBytes(),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.APPEND);

                JOptionPane.showMessageDialog(this,
                    "Costume registered (ID: " + newId + ")",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

                sizeField.setText("");
                colorField.setText("");
                priceField.setText("");
                feeField.setText("");
                periodField.setText("");
                cardLayout.show(cardPanel, "EMPLOYEE_MENU");

            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Failed to register costume.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // topButton.addActionListener(e -> cardLayout.show(cardPanel, "EMPLOYEE_MENU"));
        topButton.addActionListener(e -> {
            sizeField.setText("");
            colorField.setText("");
            priceField.setText("");
            feeField.setText("");        
            periodField.setText("");
            cardLayout.show(cardPanel, "EMPLOYEE_MENU");
        });
        
        add(Box.createVerticalStrut(20));
        add(sizeLabel);
        add(sizeField);
        add(colorLabel);
        add(colorField);
        add(priceLabel);
        add(priceField);
        add(feeLabel);
        add(feeField);
        add(periodLabel);
        add(periodField);
        add(Box.createVerticalStrut(10));
        add(registerButton);
        add(Box.createVerticalStrut(10));
        add(topButton);
    }

    private int getNextCostumeId() throws IOException {
        Path path = Paths.get("costume.csv");
        if (!Files.exists(path)) return 1;
        long lines = Files.lines(path).count();
        return (int)lines + 1;
    }
}
