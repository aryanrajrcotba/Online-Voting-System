import java.sql.*;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/voting_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Patna@1234";

    public static String authenticateUser(String username, String password) {
        // Simulated hardcoded authentication
        if ("aryanraj".equals(username) && "123456".equals(password)) return "VOTER001";
        if ("pragya".equals(username) && "1234".equals(password)) return "VOTER002";

        // Check in database
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT voter_id FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return rs.getString("voter_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int[] getVoteCounts() {
        int[] counts = new int[5];  // [BJP, Congress, AAP, JDU, RJD]
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT candidate, COUNT(*) AS vote_count FROM votes GROUP BY candidate";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String candidate = rs.getString("candidate");
                int count = rs.getInt("vote_count");
                switch (candidate) {
                    case "BJP" -> counts[0] = count;
                    case "Congress" -> counts[1] = count;
                    case "AAP" -> counts[2] = count;
                    case "JDU" -> counts[3] = count;
                    case "RJD" -> counts[4] = count;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counts;
    }

    public static boolean vote(String voterId, String voterName, String candidate) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String checkQuery = "SELECT 1 FROM votes WHERE voter_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, voterId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) return false;  // Already voted

            String insertQuery = "INSERT INTO votes (voter_id, voter_name, candidate) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, voterId);
            insertStmt.setString(2, voterName);
            insertStmt.setString(3, candidate);
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
