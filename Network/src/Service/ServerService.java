package Service;

import java.io.File;
import java.util.Vector;

import model.BEAN.Link;
import model.BEAN.Account;
import model.BO.CheckLoginBO;
import model.BO.GetDataBO;
import model.DAO.RegisterDAO;
import model.DAO.ConvertToPDFDAO;

// Spire imports
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.pdf.PdfDocument;

public class ServerService {

    // ============================================================
    //  LOGIN|username|password
    //  ⇒ "OK|id" hoặc "FAIL|msg"
    // ============================================================
    public static String handleLogin(String username, String password) {
        try {
            Account acc = CheckLoginBO.checkLogin(username, password);
            if (acc != null) {
                return "OK|" + acc.getID();
            } else {
                return "FAIL|INVALID_CREDENTIALS";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL|EXCEPTION:" + e.getMessage();
        }
    }


    // ============================================================
    //  REGISTER|username|password
    //  ⇒ "OK" hoặc "FAIL|msg"
    // ============================================================
    public static String handleRegister(String username, String password) {
        try {
            RegisterDAO dao = new RegisterDAO();
            if (dao.isUsernameExists(username)) {
                return "FAIL|USERNAME_EXISTS";
            }
            boolean ok = dao.register(username, password);
            return ok ? "OK" : "FAIL|DB_ERROR";
        } catch (Exception e) {
            return "FAIL|EXCEPTION:" + e.getMessage();
        }
    }


    // ============================================================
    //  GETDATA|userId
    //  ⇒ trả về:
    //  DATA|<n>
    //  id,type,link
    //  id,type,link
    //  ...
    //  END
    // ============================================================
    public static String handleGetData(int userId) {
        try {
            Vector<Link> list = GetDataBO.getList(userId);

            StringBuilder sb = new StringBuilder();
            sb.append("DATA|").append(list.size()).append("\n");

            for (Link l : list) {
                /*
                    id,type,link
                    type = 1 hoặc 0
                */
                sb.append(l.getID()).append(",");
                sb.append(l.isType() ? "1" : "0").append(",");
                sb.append(l.getLink()).append("\n");
            }

            sb.append("END\n");
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL|EXCEPTION:" + e.getMessage() + "\nEND\n";
        }
    }


    // ============================================================
    //  CONVERT FILE
    //
    //  RequestTask đã đọc file binary xong rồi gọi:
    //      processConvert(type, userId, originalName, tempInputPath)
    //
    //  Hàm này chỉ convert và trả:
    //      "OK|absolute_output_path" hoặc "FAIL|msg"
    //
    //  Upload/Download giao tiếp binary nằm ở RequestTask
    // ============================================================
    public static String processConvert(int type, int userId, String originalFileName, String inputPath) {
        try {
            // create downloads dir
            File downloads = new File("downloads");
            if (!downloads.exists()) downloads.mkdirs();

            // Output file name
            String base = System.currentTimeMillis() + "_" + originalFileName;
            String outPath;

            // =======================================================
            // TYPE = 1 → DOC/DOCX → PDF
            // TYPE = 0 → PDF → DOC
            // =======================================================
            if (type == 1) {  // doc -> pdf
                if (!originalFileName.toLowerCase().endsWith(".pdf")) {
                    outPath = new File(downloads, base + ".pdf").getAbsolutePath();
                } else {
                    outPath = new File(downloads, base).getAbsolutePath();
                }

                try {
                    Document doc = new Document();
                    doc.loadFromFile(inputPath);
                    doc.saveToFile(outPath, FileFormat.PDF);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "FAIL|CONVERT_ERROR:" + e.getMessage();
                }

            } else {  // pdf -> doc/docx
                if (!originalFileName.toLowerCase().endsWith(".doc")
                        && !originalFileName.toLowerCase().endsWith(".docx")) {
                    outPath = new File(downloads, base + ".doc").getAbsolutePath();
                } else {
                    outPath = new File(downloads, base).getAbsolutePath();
                }

                try {
                    PdfDocument pdf = new PdfDocument();
                    pdf.loadFromFile(inputPath);
                    // convert to DOC
                    pdf.saveToFile(outPath, com.spire.pdf.FileFormat.DOC);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "FAIL|CONVERT_ERROR:" + e.getMessage();
                }
            }

            // =======================================================
            // SAVE TO DATABASE
            // =======================================================
            ConvertToPDFDAO dao = new ConvertToPDFDAO();
            Link l = new Link();
            l.setID(userId);
            l.setLink(outPath);
            l.setType(type == 1);

            boolean saved = dao.saveLink(l);
            if (!saved) {
                return "FAIL|DB_SAVE_FAILED";
            }

            return "OK|" + outPath;

        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL|EXCEPTION:" + e.getMessage();
        }
    }


    // ============================================================
    //  DOWNLOAD: resolve file path
    //
    //  DOWNLOAD|<fileKey>
    //
    //  fileKey có thể là:
    //      - fileName
    //      - absolute path
    //      - link trong DB
    //
    //  Bạn có thể tùy chỉnh theo logic hệ thống
    // ============================================================
    public static String resolveDownload(String key) {

        // Nếu key là absolute path
        File f = new File(key);
        if (f.exists() && f.isFile()) return f.getAbsolutePath();

        // Nếu key là tên file => tìm trong thư mục downloads
        File downloads = new File("downloads");
        if (downloads.exists()) {
            File f2 = new File(downloads, key);
            if (f2.exists() && f2.isFile()) return f2.getAbsolutePath();
        }

        // TODO: hoặc tra DB để lấy link theo ID/fileName
        // ví dụ:
        // String dbPath = ConvertToPDFDAO.getFilePathByKey(key);

        return null;
    }
}
