package model.DAO;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectDB {

    private static final String URL = "jdbc:mysql://localhost:3306/ltm";
    private static final String USER = "root";
    private static final String PASS = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            System.out.println("Không thể load driver MySQL: " + e);
        }
    }

    public static Connection getConnection() {
        try {
            Connection con = DriverManager.getConnection(URL, USER, PASS);

            if (con != null && !con.isClosed()) {
                System.out.println("Kết nối MySQL thành công!");
            }

            return con;

        } catch (Exception e) {
            System.out.println("Lỗi kết nối DB: " + e);
            return null;
        }
    }

    public static void main(String[] args) {
        getConnection(); // test thử
    }
}
