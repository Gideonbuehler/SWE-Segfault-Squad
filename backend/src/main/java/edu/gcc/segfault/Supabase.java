package edu.gcc.segfault;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Supabase {

    public static final Dotenv dotenv = Dotenv.configure().directory(System.getProperty("user.dir")).load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PSWRD");

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        System.out.println(URL);
        try (Connection conn = connect()) {
            System.out.println("Connected to Supabase!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
