package Service;

import java.io.File;
import java.util.Vector;

import model.BEAN.Link;
import model.BEAN.Account;
import model.BO.CheckLoginBO;
import model.BO.GetDataBO;
import model.DAO.RegisterDAO;
import model.DAO.ConvertToPDFDAO;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.pdf.PdfDocument;

public class ServerService {

    // ============================================================
    //  LOGIN|username|password
    //  ⇒ "OK|userId" hoặc "FAIL|INVALID_CREDENTIALS"
    // ============================================================
    public static String handleLogin(String username, String password) {
        try {
            Account acc = CheckLoginBO.checkLogin(username, password);
            if (acc == null) {
                return "FAIL|INVALID_CREDENTIALS";
            }
            return "OK|" + acc.getID();
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL|EXCEPTION:" + e.getMessage();
        }
    }

    // ============================================================
    //  REGISTER|username|password
    //  ⇒ "OK|REGISTER_SUCCESS" hoặc "FAIL|USERNAME_EXISTS"
    // ============================================================
    public static String handleRegister(String username, String password) {
        try {
            RegisterDAO dao = new RegisterDAO();

            // Kiểm tra tồn tại
            if (dao.isUsernameExists(username)) {
                return "FAIL|USERNAME_EXISTS";
            }

            // Lưu user
            boolean ok = dao.register(username, password);
            if (!ok) return "FAIL|DB_ERROR";

            return "OK|REGISTER_SUCCESS";

        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL|EXCEPTION:" + e.getMessage();
        }
    }

    // ============================================================
    //  GETDATA|userId
    //  ⇒ payload:
    //      DATA|n
    //      id,type,link
    //      ...
    //      END
    // ============================================================
    public static String handleGetData(int userId) {
        try {
            Vector<Link> list = GetDataBO.getList(userId);
            StringBuilder sb = new StringBuilder();

            sb.append("DATA|").append(list.size()).append("\n");

            for (Link l : list) {
                sb.append(l.getID()).append(",");
                sb.append(l.isType() ? "1" : "0").append(",");
                sb.append(l.getLink()).append("\n");
            }

            sb.append("END\n");
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "DATA|0\nEND\n"; // đảm bảo client không bị treo
        }
    }

    // ============================================================
    //  Upload → Convert file
    //  Trả về: "OK|absolute_output_path" hoặc "FAIL|..."
    // ============================================================
    public static String processConvert(int type, int userId, String originalFileName, String inputPath) {

        try {
            File downloads = new File("downloads");
            if (!downloads.exists()) downloads.mkdirs();

            String baseName = System.currentTimeMillis() + "_" + originalFileName;
            String outPath;

            // ========================
            // TYPE 1: DOC → PDF
            // ========================
            if (type == 1) {
                if (!originalFileName.toLowerCase().endsWith(".pdf")) {
                    outPath = new File(downloads, baseName + ".pdf").getAbsolutePath();
                } else {
                    outPath = new File(downloads, baseName).getAbsolutePath();
                }

                try {
                    Document doc = new Document();
                    doc.loadFromFile(inputPath);
                    doc.saveToFile(outPath, FileFormat.PDF);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return "FAIL|CONVERT_ERROR:" + ex.getMessage();
                }

            } else {
                // ========================
                // TYPE 0: PDF → DOC
                // ========================
                if (!originalFileName.toLowerCase().endsWith(".doc") &&
                    !originalFileName.toLowerCase().endsWith(".docx")) {
                    outPath = new File(downloads, baseName + ".doc").getAbsolutePath();
                } else {
                    outPath = new File(downloads, baseName).getAbsolutePath();
                }

                try {
                    PdfDocument pdf = new PdfDocument();
                    pdf.loadFromFile(inputPath);
                    pdf.saveToFile(outPath, com.spire.pdf.FileFormat.DOC);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return "FAIL|CONVERT_ERROR:" + ex.getMessage();
                }
            }

            // ========================
            // Lưu vào DB
            // ========================
            ConvertToPDFDAO dao = new ConvertToPDFDAO();
            Link l = new Link();
            l.setID(userId);
            l.setType(type == 1);
            l.setLink(outPath);

            if (!dao.saveLink(l)) {
                return "FAIL|DB_SAVE_FAILED";
            }

            return "OK|" + outPath;

        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL|EXCEPTION:" + e.getMessage();
        }
    }

    // ============================================================
    //  DOWNLOAD|key
    //  ⇒ trả về absolute file path hoặc null
    // ============================================================
    public static String resolveDownload(String key) {

        // 1. Absolute path
        File f = new File(key);
        if (f.exists() && f.isFile()) return f.getAbsolutePath();

        // 2. Tìm trong folder downloads
        File downloads = new File("downloads");
        if (downloads.exists()) {
            File f2 = new File(downloads, key);
            if (f2.exists() && f2.isFile()) return f2.getAbsolutePath();
        }

        // 3. (Tuỳ chọn) tìm trong database bằng key
        // ví dụ:
        // String dbPath = ConvertToPDFDAO.getPathByKey(key);

        return null;
    }
}
