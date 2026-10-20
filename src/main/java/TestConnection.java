
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@//localhost:1521/XE";
        String username = "SYSTEM";
        String password = "123456";

        try {
            Class.forName("oracle.jdbc.OracleDriver");

            try (Connection con =
                     DriverManager.getConnection(url, username, password);
                 Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT COUNT(*) FROM APP_USERS")) {

                if (rs.next()) {
                    System.out.println("Oracle connection successful!");
                    System.out.println(
                        "Users found: " + rs.getInt(1));
                }
            }
        } catch (Exception e) {
            System.out.println("Connection failed:");
            e.printStackTrace();
        }
    }
}
