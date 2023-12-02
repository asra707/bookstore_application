import java.sql.*;

public class JDBC {
    public static void main(String[] args) throws Exception{

        //String sql = "select authorname from authors where authorid=3";
        String url = "jdbc:postgresql://localhost:5432/bookstoredb";
        String username = "postgres";
        String password = "postmynutsql7";

        Connection con = null;
        try {
            con = DriverManager.getConnection(url, username, password);
            if (con != null) {
                System.out.println("Connected to the database!!!!!");
                // You can perform your database operations here
            }
        } catch (SQLException e) {
            System.out.println("Connection failed. Error: " + e.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

    }
}