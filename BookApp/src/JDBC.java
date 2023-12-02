import java.sql.*;

public class JDBC {

    public static void main(String[] args) {

        //connection parameters
        String url = "jdbc:postgresql://localhost:5432/bookstoredb";
        String username = "postgres";
        String password = "postmynutsql7";

        Connection con = null;
        try {
            con = DriverManager.getConnection(url, username, password);
            if (con != null) {
                System.out.println("FINALLY connected to the database!!!!!!!");

                // add operations

                //insertAuthor(con, 6,"Tatsuki Fujimoto"); done

                //insertBook(con, 7, "chainsawman",6, 21); done

                //retrieveBookData(con);

                //updateBook(con,5, "Do not kill the bird.", 8); done

                // deleteOrders7Book(con, 5); done

            }
        } catch (SQLException e) {
            System.out.println("Connection failed. Error: " + e.getMessage() + " :(");
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage() + " :(");
            }
        }

    }


    //create op
    private static void insertAuthor(Connection con, int authorID, String authorName) throws SQLException {
        String insertAuthorQuery = "insert into Authors (authorID, authorName) values (?, ?)";
        try (PreparedStatement preps = con.prepareStatement(insertAuthorQuery)) {
            preps.setInt(1, authorID);
            preps.setString(2, authorName);
            preps.executeUpdate();
            System.out.println("Author added!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage() + " :(");
        }
    }

    private static void insertBook(Connection con, int bookID, String title, int authorID, int stock) throws SQLException {
        String sql = "insert into Books (bookID, title, authorID, stock) values (?, ?, ?, ?)";
        try (PreparedStatement preps = con.prepareStatement(sql)) {
            preps.setInt(1, bookID);
            preps.setString(2, title);
            preps.setInt(3, authorID);
            preps.setInt(4, stock);
            preps.executeUpdate();
            System.out.println("Book added!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage() + " :(");
        }
    }


    // retrieve op
    private static void retrieveBookData(Connection con) {
        String sql = "select b.title, a.authorName, o.orderDate, o.quantity " +
                "from Books b " +
                "inner join Authors a on b.authorID = a.authorID " +
                "left join Orders o on b.bookID = o.bookID";

        try (Statement stat = con.createStatement();
             ResultSet res = stat.executeQuery(sql)) {
            System.out.println("Book Data:");
            while (res.next()) {
                String title = res.getString("title");
                String authorName = res.getString("authorName");
                Date orderDate = res.getDate("orderDate");
                int quantity = res.getInt("quantity");

                System.out.println("Title: " + title + ", Author: " + authorName + ", Order Date: " + orderDate + ", Quantity: " + quantity);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage() + " :(");
        }
    }


    //update op
    private static void updateBook(Connection con, int bookID, String title, int stock) throws SQLException {
        String updateBookQuery = "update Books set title = ?, stock = ? where bookID = ?";
        try (PreparedStatement preps = con.prepareStatement(updateBookQuery)) {
            preps.setString(1, title);
            preps.setInt(2, stock);
            preps.setInt(3, bookID);
            int rows = preps.executeUpdate();
            if (rows > 0) {
                System.out.println("Book details updated.");
            } else {
                System.out.println("No book was found using the provided ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage() + " :(");
        }
    }


    //delete op
    // Delete op ERROR
    private static void deleteOrders7Book(Connection con, int bookID) throws SQLException {
        String sqlOrders = "delete from Orders where bookID = ?";
        try (PreparedStatement preps = con.prepareStatement(sqlOrders)) {
            preps.setInt(1, bookID);
            int rowso = preps.executeUpdate();
            if (rowso > 0) {
                System.out.println("Orders deleted.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage() + " :(");
            return;
        }

        String sqlBook = "delete from Books where bookID = ?";
        try (PreparedStatement preps = con.prepareStatement(sqlBook)) {
            preps.setInt(1, bookID);
            int rowsb= preps.executeUpdate();
            if (rowsb > 0) {
                System.out.println("Book deleted.");
            } else {
                System.out.println("No book was found using the provided ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage() + " :(");
        }
    }




}






