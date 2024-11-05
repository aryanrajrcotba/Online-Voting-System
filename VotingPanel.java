import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VotingPanel extends JPanel {
    private final String voterId;
    private JTextField voterNameField;
    private JComboBox<String> candidateComboBox;
    private JLabel bjpVotes, congressVotes, aapVotes, jduVotes, rjdVotes;

    public VotingPanel(String voterId) {
        this.voterId = voterId;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        setupFormPanel();
        setupVoteCountPanel();
        loadVoteCounts();
    }

    private void setupFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.add(new JLabel("Voter ID:"));
        JTextField voterIdField = new JTextField(voterId);
        voterIdField.setEnabled(false);
        formPanel.add(voterIdField);

        formPanel.add(new JLabel("Your Name:"));
        voterNameField = new JTextField();
        formPanel.add(voterNameField);

        formPanel.add(new JLabel("Select Candidate:"));
        candidateComboBox = new JComboBox<>(new String[]{"Choose a candidate", "BJP", "Congress", "AAP", "JDU", "RJD"});
        formPanel.add(candidateComboBox);

        JButton voteButton = new JButton("Vote Now");
        voteButton.addActionListener(new VoteButtonListener());
        formPanel.add(voteButton);
        
        add(formPanel);
    }

    private void setupVoteCountPanel() {
        JPanel voteCountPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        bjpVotes = new JLabel("0");
        congressVotes = new JLabel("0");
        aapVotes = new JLabel("0");
        jduVotes = new JLabel("0");
        rjdVotes = new JLabel("0");

        voteCountPanel.add(new JLabel("BJP:"));
        voteCountPanel.add(bjpVotes);
        voteCountPanel.add(new JLabel("Congress:"));
        voteCountPanel.add(congressVotes);
        voteCountPanel.add(new JLabel("AAP:"));
        voteCountPanel.add(aapVotes);
        voteCountPanel.add(new JLabel("JDU:"));
        voteCountPanel.add(jduVotes);
        voteCountPanel.add(new JLabel("RJD:"));
        voteCountPanel.add(rjdVotes);

        add(voteCountPanel);
    }

    private void loadVoteCounts() {
        int[] counts = DatabaseHelper.getVoteCounts();
        bjpVotes.setText(String.valueOf(counts[0]));
        congressVotes.setText(String.valueOf(counts[1]));
        aapVotes.setText(String.valueOf(counts[2]));
        jduVotes.setText(String.valueOf(counts[3]));
        rjdVotes.setText(String.valueOf(counts[4]));
    }

    private class VoteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String voterName = voterNameField.getText();
            String selectedCandidate = (String) candidateComboBox.getSelectedItem();

            if (!DatabaseHelper.vote(voterId, voterName, selectedCandidate)) {
                JOptionPane.showMessageDialog(null, "You have already voted!");
            } else {
                loadVoteCounts();
            }
        }
    }
}
