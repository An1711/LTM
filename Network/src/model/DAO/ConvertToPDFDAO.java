package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.BEAN.Link;

public class ConvertToPDFDAO {
    
    /**
     * Lưu thông tin link download vào database
     * @param link đối tượng Link chứa thông tin
     * @return true nếu lưu thành công, false nếu thất bại
     */
    public boolean saveLink(Link link) {
        String sql = "INSERT INTO data (userID, link, type) VALUES (?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = ConnectDB.getConnection();
            if (conn == null) {
                System.err.println("Không thể kết nối database!");
                return false;
            }
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, link.getID());
            pstmt.setString(2, link.getLink());
            pstmt.setBoolean(3, link.isType());
            
            int rowsAffected = pstmt.executeUpdate();
            
            System.out.println("Đã lưu link vào database: " + link.getLink());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lưu vào database: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, conn);
        }
    }
    
    /**
     * Lấy thông tin link theo ID
     * @param id userID của link
     * @return đối tượng Link hoặc null nếu không tìm thấy
     */
    public Link getLinkById(int id) {
        String sql = "SELECT * FROM data WHERE userID = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectDB.getConnection();
            if (conn == null) return null;
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Link link = new Link();
                link.setID(rs.getInt("id"));
                link.setLink(rs.getString("link"));
                link.setType(rs.getBoolean("type"));
                return link;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        
        return null;
    }
    
    public List<Link> getListLinkById(int id) {
        String sql = "SELECT * FROM data WHERE userID = ?";
        List<Link> list = new ArrayList<>();

        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Link link = new Link();
                    link.setID(rs.getInt("userID"));
                    link.setLink(rs.getString("link"));
                    link.setType(rs.getBoolean("type"));
                    list.add(link);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    
    /**
     * Lấy thông tin link theo đường dẫn link
     * @param downloadLink đường link download
     * @return đối tượng Link hoặc null nếu không tìm thấy
     */
    public Link getLinkByPath(String downloadLink) {
        String sql = "SELECT * FROM data WHERE link = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectDB.getConnection();
            if (conn == null) return null;
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, downloadLink);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Link link = new Link();
                link.setID(rs.getInt("ID"));
                link.setLink(rs.getString("link"));
                link.setType(rs.getBoolean("type"));
                return link;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        
        return null;
    }
    
    /**
     * Lấy tất cả link đã chuyển đổi
     * @return danh sách các Link
     */
    public List<Link> getAllLinks() {
        String sql = "SELECT * FROM data";
        List<Link> linkList = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectDB.getConnection();
            if (conn == null) return linkList;
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Link link = new Link();
                link.setID(rs.getInt("ID"));
                link.setLink(rs.getString("link"));
                link.setType(rs.getBoolean("type"));
                linkList.add(link);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        
        return linkList;
    }
    
    /**
     * Lấy danh sách link theo loại chuyển đổi
     * @param type true: doc→pdf, false: pdf→doc
     * @return danh sách Link
     */
    public List<Link> getLinksByType(boolean type) {
        String sql = "SELECT * FROM data";
        List<Link> linkList = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectDB.getConnection();
            if (conn == null) return linkList;
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, type);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Link link = new Link();
                link.setID(rs.getInt("ID"));
                link.setLink(rs.getString("link"));
                link.setType(rs.getBoolean("type"));
                linkList.add(link);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        
        return linkList;
    }
    
    /**
     * Xóa link khỏi database
     * @param linkId ID của link cần xóa
     * @return true nếu xóa thành công
     */
    public boolean deleteLink(int linkId) {
        String sql = "DELETE FROM data WHERE userID = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = ConnectDB.getConnection();
            if (conn == null) return false;
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, linkId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, pstmt, conn);
        }
    }
    
    /**
     * Cập nhật thông tin link
     * @param link đối tượng Link cần cập nhật
     * @return true nếu cập nhật thành công
     */
    public boolean updateLink(Link link) {
        String sql = "UPDATE data SET link = ?, type = ? WHERE userID = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = ConnectDB.getConnection();
            if (conn == null) return false;
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, link.getLink());
            pstmt.setBoolean(2, link.isType());
            pstmt.setInt(3, link.getID());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, pstmt, conn);
        }
    }
    
    /**
     * Đếm tổng số file đã chuyển đổi
     * @return số lượng file
     */
    public int getTotalCount() {
        String sql = "SELECT COUNT(*) as total FROM data";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectDB.getConnection();
            if (conn == null) return 0;
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        
        return 0;
    }
    
    /**
     * Đếm số lượng theo loại
     * @param type true: doc→pdf, false: pdf→doc
     * @return số lượng
     */
    public int getCountByType(boolean type) {
        String sql = "SELECT COUNT(*) as total FROM data WHERE type = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectDB.getConnection();
            if (conn == null) return 0;
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, type);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        
        return 0;
    }
    
    /**
     * Đóng các resources
     */
    private void closeResources(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}