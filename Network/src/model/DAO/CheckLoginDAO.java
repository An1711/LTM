package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.BEAN.Account;

public class CheckLoginDAO {

    public static Account checkLogin(String username, String password) {
        String sql = "SELECT * FROM account WHERE username = ? AND password = ?";
        try (
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con != null ? con.prepareStatement(sql) : null
        ) {
            if (ps == null) return null;

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Account acc = new Account();
                    acc.setUsername(rs.getString("username"));
                    acc.setPasword(rs.getString("password")); 
                    acc.setID(rs.getInt("ID"));
                    return acc;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; 
    }
}
