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
                // operations

                //insertAuthor(con, 7,"John le Carre");

                //insertBook(con, 8, "A Legacy of Spies", 7, 0);

                //insertCustomer(con, 6, "Joe Mama", "joemama@gamil.com", "15 StreetName, CityName");

                //insertOrder(con, 11, 6, 7, "2023-12-04", 1); done

                //Transaction(con, 13, 6, 8, "2023-12-04", 1);

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

    private static void insertCustomer(Connection con, int customerID, String customerName, String email, String address) throws SQLException {
        String sql = "insert into Customers (customerID, customerName, email, address) values (?, ?, ?, ?)";
        try (PreparedStatement preps = con.prepareStatement(sql)) {
            preps.setInt(1, customerID);
            preps.setString(2, customerName);
            preps.setString(3, email);
            preps.setString(4, address);
            preps.executeUpdate();
            System.out.println("Customer added!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage() + " :(");
        }
    }

    //trans
    private static void transaction(Connection con, int orderID, int customerID, int bookID, String orderDate, int quantity) throws SQLException {
        String sql = "select stock from Books where bookID = ?";
        try (PreparedStatement check = con.prepareStatement(sql)) {
            check.setInt(1, bookID);
            ResultSet res = check.executeQuery();
            if (res.next()) {
                int stock = res.getInt("stock");
                if (stock >= quantity) {
                    con.setAutoCommit(false);
                    String insertOrder = "insert into Orders (orderID, customerID, bookID, orderDate, quantity) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement insertOrderPrep = con.prepareStatement(insertOrder)) {
                        insertOrderPrep.setInt(1, orderID);
                        insertOrderPrep.setInt(2, customerID);
                        insertOrderPrep.setInt(3, bookID);
                        insertOrderPrep.setDate(4, Date.valueOf(orderDate));
                        insertOrderPrep.setInt(5, quantity);
                        insertOrderPrep.executeUpdate();
                        System.out.println("Order added!");

                        // dec
                        String sqlup = "update Books SET stock = stock - ? where bookID = ?";
                        try (PreparedStatement updateStock= con.prepareStatement(sqlup)) {
                            updateStock.setInt(1, quantity);
                            updateStock.setInt(2, bookID);
                            updateStock.executeUpdate();
                            System.out.println("Book stock updated!");

                            con.commit();
                        } catch (SQLException e) {
                            con.rollback();
                            System.out.println("Error updating stock: " + e.getMessage() + " :(");
                        }
                    } catch (SQLException e) {
                        con.rollback();
                        System.out.println("Error inserting order: " + e.getMessage() + " :(");
                    }
                } else {
                    System.out.println("Not enough books available in stock.");
                }
            } else {
                System.out.println("Book was not found!");
            }
        } catch (SQLException e) {
            System.out.println("Error checking book availability: " + e.getMessage() + " :(");
        } finally {
            con.setAutoCommit(true);
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
        String sql = "update Books set title = ?, stock = ? where bookID = ?";
        try (PreparedStatement preps = con.prepareStatement(sql)) {
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






