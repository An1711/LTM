package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterDAO {

    public boolean isUsernameExists(String username) {
        String sql = "SELECT 1 FROM account WHERE username = ?";

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con != null ? con.prepareStatement(sql) : null
        ) {
            if (ps == null) return false;

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();  
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean register(String username, String password) {
        String sql = "INSERT INTO account(username, password) VALUES(?, ?)";

        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con != null ? con.prepareStatement(sql) : null
        ) {
            if (ps == null) return false;

            ps.setString(1, username);
            ps.setString(2, password);

            int rows = ps.executeUpdate();
            return rows > 0;   

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
