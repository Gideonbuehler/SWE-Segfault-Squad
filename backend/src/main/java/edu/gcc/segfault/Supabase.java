package edu.gcc.segfault;
import io.github.cdimascio.dotenv.Dotenv;
import net.bytebuddy.dynamic.scaffold.MethodRegistry;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

public class Supabase {

    public static final Dotenv dotenv = loadDotenv();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PSWRD");
    private static Connection conn;

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public Supabase() {

        try {
            conn = connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        setUpTable();
    }

    private void setUpTable() {
        //Combines the searching table data into one cell so that we can
        //search for substrings just once.
        String ps = "ALTER TABLE CourseOfferings\n" +
                "ADD COLUMN search_text TEXT;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(ps);
            pstmt.executeQuery();
        } catch (SQLException e) {

        }
        String statement = "UPDATE CourseOfferings\n" +
                "SET search_text = name || ' ' || description || ' ' || professor || ' ' || subject || ' ' || number;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(statement);
            pstmt.executeQuery();
        } catch (SQLException e) {

        }

    }

    public static void main(String[] args) throws SQLException {
        //connecting to the database
        System.out.println(System.getProperty("user.dir"));
        System.out.println(URL);
        try {
            conn = connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //Example Query
        String preparedStatement = "SELECT * FROM CourseOfferings WHERE name ILIKE '%intro%'";
        PreparedStatement pstmt = conn.prepareStatement(preparedStatement);
        Statement s = conn.createStatement();

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            System.out.println("code: " + rs.getString("subject") + rs.getString("number") + " course name: " + rs.getString("name"));
        }

    }

    public Connection getConn() {
        return conn;
    }

    public static Dotenv loadDotenv() {
        File dir = new File(System.getProperty("user.dir"));

        while (dir != null) {
            File envFile = new File(dir, ".env");
            if (envFile.exists()) {
                return Dotenv.configure()
                        .directory(dir.getAbsolutePath())
                        .load();
            }
            dir = dir.getParentFile();
        }
        throw new RuntimeException(".env file not found");
    }
}
