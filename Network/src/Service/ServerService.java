package Service;

import java.io.File;
import java.util.Vector;
import model.BEAN.Link;
import model.DAO.RegisterDAO;
import model.DAO.CheckLoginDAO;
import model.BO.CheckLoginBO;
import model.BO.GetDataBO;
import model.DAO.ConvertToPDFDAO;

// spire imports
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.pdf.PdfDocument;

public class ServerService {

    public static String processCommand(String message) {
        if (message == null || message.trim().isEmpty()) return "FAIL|EMPTY";

        String[] parts = message.split("\\|", 3);
        String cmd = parts[0].trim().toUpperCase();

        try {
            switch (cmd) {
                case "REGISTER": {
                    if (parts.length < 3) return "FAIL|BAD_FORMAT";
                    String username = parts[1];
                    String password = parts[2];
                    RegisterDAO dao = new RegisterDAO();
                    if (dao.isUsernameExists(username)) {
                        return "FAIL|USERNAME_EXISTS";
                    }
                    boolean ok = dao.register(username, password);
                    return ok ? "OK" : "FAIL|DB_ERROR";
                }
                case "LOGIN": {
                    System.out.println("Processing LOGIN command");
                    if (parts.length < 3) return "FAIL|BAD_FORMAT";
                    String username = parts[1];
                    String password = parts[2];
                    model.BEAN.Account acc = CheckLoginBO.checkLogin(username, password);
                    if (acc != null) {
                        return "OK|" + acc.getID();
                    } else {
                        return "FAIL|INVALID_CREDENTIALS";
                    }
                }
                case "GETDATA": {
                    if (parts.length < 2) return "FAIL|BAD_FORMAT";
                    int userId;
                    try { userId = Integer.parseInt(parts[1].trim()); } catch (Exception e) { return "FAIL|BAD_USERID"; }
                    Vector<Link> list = GetDataBO.getList(userId);
                    StringBuilder sb = new StringBuilder();
                    sb.append("LINKS|");
                    boolean first = true;
                    for (Link l : list) {
                        if (!first) sb.append(";");
                        first = false;
                        int id = l.getID();
                        String type = l.isType() ? "1" : "0";
                        String link = l.getLink();
                        // escape semicolons or commas? keep simple
                        sb.append(id).append(",").append(type).append(",").append(link);
                    }
                    return sb.toString();
                }
                default:
                    return "FAIL|UNKNOWN_COMMAND";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL|EXCEPTION:" + e.getMessage();
        }
    }

        public static String processConvert(int type, int userId, String originalFileName, String inputPath) {
            try {
                // create downloads dir
                File downloads = new File("downloads");
                if (!downloads.exists()) downloads.mkdirs();

                String outNameBase = System.currentTimeMillis() + "_" + originalFileName;
                String outPath = null;

                if (type == 1) { // doc -> pdf
                    if (!originalFileName.toLowerCase().endsWith(".pdf")) {
                        outPath = new File(downloads, outNameBase + ".pdf").getAbsolutePath();
                    } else {
                        outPath = new File(downloads, outNameBase).getAbsolutePath();
                    }
                    try {
                        Document doc = new Document();
                        doc.loadFromFile(inputPath);
                        doc.saveToFile(outPath, FileFormat.PDF);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "FAIL|CONVERT_ERROR:" + e.getMessage();
                    }
                } else { // pdf -> doc
                    if (!originalFileName.toLowerCase().endsWith(".doc") && !originalFileName.toLowerCase().endsWith(".docx")) {
                        outPath = new File(downloads, outNameBase + ".doc").getAbsolutePath();
                    } else {
                        outPath = new File(downloads, outNameBase).getAbsolutePath();
                    }
                    try {
                        PdfDocument pdf = new PdfDocument();
                        pdf.loadFromFile(inputPath);
                        pdf.saveToFile(outPath, com.spire.pdf.FileFormat.DOC);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "FAIL|CONVERT_ERROR:" + e.getMessage();
                    }
                }

                // persist link into database
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
}
