import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class VotingSystem extends JFrame {
    private JTextField voterIdField, voterNameField;
    private JComboBox<String> candidateComboBox;
    private JLabel bjpVotes, congressVotes, aapVotes, jduVotes, rjdVotes;
    private String loggedInVoterId;

    public VotingSystem() {
        showLoginScreen(); // First show the login screen
    }

    // Method to show the login screen
    private void showLoginScreen() {
        setTitle("Login to Voting System");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3, 2, 10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter both username and password!");
                    return;
                }

                // Hardcoded username and password for two users
                if ((username.equals("aryanraj") && password.equals("123456")) ||
                        (username.equals("pragya") && password.equals("1234"))) {

                    // Assign Voter ID based on the username
                    loggedInVoterId = username.equals("aryanraj") ? "VOTER001" : "VOTER002";
                    showVotingScreen();
                    return;
                }

                // Fallback to database authentication if needed
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/voting_db", "root", "Patna@1234")) {
                    String query = "SELECT * FROM users WHERE username = ? AND password = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        loggedInVoterId = rs.getString("voter_id");
                        showVotingScreen();
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid credentials! Please try again.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        loginPanel.add(new JLabel()); // Empty cell for alignment
        loginPanel.add(loginButton);

        add(loginPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    // Method to show the voting screen after login
    private void showVotingScreen() {
        getContentPane().removeAll(); // Clear the login screen
        setTitle("Online Voting System");
        setSize(500, 600);
        setLayout(new BorderLayout());

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Online Voting System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        container.add(titleLabel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 2, 10, 10));

        formPanel.add(new JLabel("Voter ID:"));
        voterIdField = new JTextField(loggedInVoterId);
        voterIdField.setEnabled(false); // Voter ID is pre-filled from login and cannot be changed
        formPanel.add(voterIdField);

        formPanel.add(new JLabel("Your Name:"));
        voterNameField = new JTextField();
        formPanel.add(voterNameField);

        formPanel.add(new JLabel("Select Candidate:"));
        candidateComboBox = new JComboBox<>(new String[]{"Choose a candidate", "BJP", "Congress", "AAP", "JDU", "RJD"});
        formPanel.add(candidateComboBox);

        JButton voteButton = new JButton("Vote Now");
        voteButton.setBackground(new Color(76, 175, 80));
        voteButton.setForeground(Color.WHITE);
        voteButton.addActionListener(new VoteButtonListener());
        formPanel.add(voteButton);

        container.add(formPanel);

        JPanel voteCountPanel = new JPanel();
        voteCountPanel.setLayout(new GridLayout(6, 2, 10, 10));

        voteCountPanel.add(new JLabel("Vote Count", SwingConstants.CENTER));
        voteCountPanel.add(new JLabel("")); // Empty label for alignment

        voteCountPanel.add(new JLabel("BJP:"));
        bjpVotes = new JLabel("0");
        voteCountPanel.add(bjpVotes);

        voteCountPanel.add(new JLabel("Congress:"));
        congressVotes = new JLabel("0");
        voteCountPanel.add(congressVotes);

        voteCountPanel.add(new JLabel("AAP:"));
        aapVotes = new JLabel("0");
        voteCountPanel.add(aapVotes);

        voteCountPanel.add(new JLabel("JDU:"));
        jduVotes = new JLabel("0");
        voteCountPanel.add(jduVotes);

        voteCountPanel.add(new JLabel("RJD:"));
        rjdVotes = new JLabel("0");
        voteCountPanel.add(rjdVotes);

        container.add(voteCountPanel);

        add(container, BorderLayout.CENTER);
        setVisible(true);

        loadVoteCounts();  // Load initial vote counts from the database
    }

    // ActionListener for the Vote button
    private class VoteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String voterName = voterNameField.getText();
            String selectedCandidate = (String) candidateComboBox.getSelectedItem();

            if (voterName.isEmpty() || "Choose a candidate".equals(selectedCandidate)) {
                JOptionPane.showMessageDialog(null, "Please fill in all fields!");
                return;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/voting_db", "root", "Patna@1234")) {
                System.out.println("Database connected successfully.");

                // Check if voter has already voted
                String checkVoter = "SELECT * FROM votes WHERE voter_id = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkVoter);
                checkStmt.setString(1, loggedInVoterId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "You have already voted!");
                } else {
                    // Insert vote into the database
                    String insertVote = "INSERT INTO votes (voter_id, voter_name, candidate) VALUES (?, ?, ?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insertVote);
                    insertStmt.setString(1, loggedInVoterId);
                    insertStmt.setString(2, voterName);
                    insertStmt.setString(3, selectedCandidate);
                    insertStmt.executeUpdate();

                    System.out.println("Vote added for candidate: " + selectedCandidate);
                    updateVoteCount(selectedCandidate);
                    JOptionPane.showMessageDialog(null, "Vote added successfully for " + selectedCandidate + "!");

                    voterNameField.setText("");
                    candidateComboBox.setSelectedIndex(0);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Method to load vote counts from the database and display them
    private void loadVoteCounts() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/voting_db", "root", "Patna@1234")) {
            String query = "SELECT candidate, COUNT(*) AS vote_count FROM votes GROUP BY candidate";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Reset counts
            bjpVotes.setText("0");
            congressVotes.setText("0");
            aapVotes.setText("0");
            jduVotes.setText("0");
            rjdVotes.setText("0");

            // Update counts based on the results
            while (rs.next()) {
                String candidate = rs.getString("candidate");
                int voteCount = rs.getInt("vote_count");

                switch (candidate) {
                    case "BJP" -> bjpVotes.setText(String.valueOf(voteCount));
                    case "Congress" -> congressVotes.setText(String.valueOf(voteCount));
                    case "AAP" -> aapVotes.setText(String.valueOf(voteCount));
                    case "JDU" -> jduVotes.setText(String.valueOf(voteCount));
                    case "RJD" -> rjdVotes.setText(String.valueOf(voteCount));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateVoteCount(String candidate) {
        loadVoteCounts();  // Reload vote counts from the database to update the UI
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VotingSystem::new);
    }
}
