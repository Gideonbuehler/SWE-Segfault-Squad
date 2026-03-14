package edu.gcc.segfault;
import io.github.cdimascio.dotenv.Dotenv;
import net.bytebuddy.dynamic.scaffold.MethodRegistry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

public class Supabase {

    public static final Dotenv dotenv = Dotenv.configure().directory(System.getProperty("user.dir")).load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PSWRD");
    private static Connection conn;

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) throws SQLException {
        System.out.println(System.getProperty("user.dir"));
        System.out.println(URL);
        try {
            conn = connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        String preparedStatement = "SELECT * FROM CourseOfferings WHERE code LIKE %Intro%";
        PreparedStatement pstmt = conn.prepareStatement(preparedStatement);
        Statement s = conn.createStatement();

        ResultSet rs = pstmt.executeQuery();
        while(rs.next()){
            System.out.println("code: " + rs.getString("code") + rs.getString("number") + " course name: " + rs.getString("name"));
        }

    }
}
