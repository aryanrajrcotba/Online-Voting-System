# Online-Voting-System
This project is a Java-based Online Voting System that provides a secure, interactive platform for users to cast their votes and view real-time vote counts. Developed using Java Swing for the graphical user interface (GUI) and MySQL for backend database management, this application handles essential voting functionalities including user authentication, vote submission, and dynamic vote count updates.

# Features
# User Authentication:
Dual authentication options include hardcoded user access for testing and a MySQL-based login for production use.
Each user is assigned a unique voter ID, displayed on the voting screen for secure identification.
# Vote Submission:
Users can cast their vote by selecting a candidate from a dropdown list after entering their name.
The system checks if the user has already voted, ensuring “one person, one vote” integrity.
# Real-Time Vote Count Display:
Vote counts for each candidate (e.g., BJP, Congress, AAP, JDU, RJD) are displayed on the voting screen.
The system dynamically updates vote counts after each submission, offering users instant feedback on election progress.
Technologies Used
Java Swing for GUI
MySQL for database management
JDBC for database connectivity
PreparedStatement for SQL injection prevention

