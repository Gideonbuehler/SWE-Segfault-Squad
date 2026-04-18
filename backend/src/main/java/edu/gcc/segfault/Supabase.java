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
            setUpTable();
        } catch (SQLException e) {
            throw new RuntimeException("This is where it's failing");
        }

    }

    private void setUpTable() {
        //Combines the searching table data into one cell so that we can
        //search for substrings just once.
        String ps = "ALTER TABLE CourseOfferings2\n" +
                "ADD COLUMN IF NOT EXISTS search_text TEXT;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(ps);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String statement = "UPDATE CourseOfferings2\n" +
                "SET search_text =\n" +
                "    COALESCE(name, '') || ' ' ||\n" +
                "    COALESCE(description, '') || ' ' ||\n" +
                "    COALESCE(faculty, '') || ' ' ||\n" +
                "    COALESCE(subject, '') || ' ' ||\n" +
                "    COALESCE(number::text, '');";
        try {
            PreparedStatement pstmt = conn.prepareStatement(statement);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
    //AI Generated
    public static Dotenv loadDotenv() {
        File dir = new File(System.getProperty("user.dir"));
        System.out.println("Starting dir: " + dir.getAbsolutePath());

        while (dir != null) {
            System.out.println("Checking: " + dir.getAbsolutePath());
            File envFile = new File(dir, ".env");

            if (envFile.exists()) {
                System.out.println("FOUND .env at: " + dir);
                return Dotenv.configure()
                        .directory(dir.getPath()) // <-- use getPath(), not getAbsolutePath()
                        .ignoreIfMalformed()
                        .ignoreIfMissing()
                        .load();
            }
            dir = dir.getParentFile();
        }
        throw new RuntimeException(".env file not found");
    }
}
