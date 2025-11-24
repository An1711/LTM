package controller;

import java.io.File;
import java.util.Vector;
import model.BEAN.Account;
import model.BEAN.Link;
import model.BO.*;

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
            RegisterBO bo = new RegisterBO();

            // Kiểm tra tồn tại
            if (bo.isUsernameExists(username)) {
                return "FAIL|USERNAME_EXISTS";
            }

            // Lưu user
            boolean ok = bo.register(username, password);
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
    	if (WorkerManager.isWorkerOnline()) {
            String workerResult = WorkerManager.sendConvertJob(type, userId, originalFileName, inputPath);

            if (workerResult != null && workerResult.startsWith("OK|")) {
                return workerResult; // worker xử lý thành công
            }
        }
    	
    	ConvertToPDFBO bo = new ConvertToPDFBO();
    	String rs = bo.processConvert(type, userId, originalFileName, inputPath);
    	return rs;
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
            // If key contains a folder prefix like "downloads/filename", use only the file name
            String nameOnly = new File(key).getName();
            File f2 = new File(downloads, nameOnly);
            if (f2.exists() && f2.isFile()) return f2.getAbsolutePath();
        }

        // 3. (Tuỳ chọn) tìm trong database bằng key
        // ví dụ:
        // String dbPath = ConvertToPDFDAO.getPathByKey(key);

        return null;
    }
}
