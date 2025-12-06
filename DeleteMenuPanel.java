import javax.swing.*;
import java.awt.*;
import javax.swing.Box;
import java.util.Arrays;

public class DeleteMenuPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel     cardPanel;
    private JButton    deleteCostumeButton;
    private JButton    deleteMemberButton;
    private JButton    deleteEmployeeButton;
    private JButton    topButton;

    public DeleteMenuPanel(CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel  = cardPanel;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalStrut(20));
        deleteCostumeButton = new JButton("Delete costume");
        deleteMemberButton  = new JButton("Delete member");
        deleteEmployeeButton= new JButton("Delete employee");
        topButton           = new JButton("Top");

        for (JButton b : Arrays.asList(
                deleteCostumeButton,
                deleteMemberButton,
                deleteEmployeeButton,
                topButton)) {
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(b);
            add(Box.createVerticalStrut(10));
        }


        deleteCostumeButton.addActionListener(e -> {
            DeleteCostumePanel p = new DeleteCostumePanel(cardLayout, cardPanel);
            cardPanel.add(p, "DELETE_COSTUME");
            cardLayout.show(cardPanel, "DELETE_COSTUME");
        });
        deleteMemberButton.addActionListener(e -> {
            DeleteMemberPanel p = new DeleteMemberPanel(cardLayout, cardPanel);
            cardPanel.add(p, "DELETE_MEMBER");
            cardLayout.show(cardPanel, "DELETE_MEMBER");
        });
        deleteEmployeeButton.addActionListener(e -> {
            DeleteEmployeePanel p = new DeleteEmployeePanel(cardLayout, cardPanel);
            cardPanel.add(p, "DELETE_EMPLOYEE");
            cardLayout.show(cardPanel, "DELETE_EMPLOYEE");
        });
        topButton.addActionListener(e ->
            cardLayout.show(cardPanel, "EMPLOYEE_MENU"));
    }    
}
