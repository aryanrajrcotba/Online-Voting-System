import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {
    private final VotingSystem parentFrame;

    public LoginPanel(VotingSystem frame) {
        this.parentFrame = frame;
        setLayout(new GridLayout(3, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                String voterId = DatabaseHelper.authenticateUser(username, password);
                if (voterId != null) {
                    parentFrame.setLoggedInVoterId(voterId);
                    parentFrame.showVotingScreen();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid credentials! Please try again.");
                }
            }
        });

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(new JLabel());
        add(loginButton);
    }
}
