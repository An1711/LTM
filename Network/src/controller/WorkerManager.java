package controller;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import model.BEAN.Link;
import model.DAO.ConvertToPDFDAO;

public class WorkerManager {

    private static final String WORKER_HOST = "localhost";
    private static final int WORKER_PORT = 9999;

    // ============================================================
    // KIỂM TRA WORKER ONLINE
    // ============================================================
    public static boolean isWorkerOnline() {
        try (Socket s = new Socket(WORKER_HOST, WORKER_PORT)) {
            // gửi PING
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            dos.writeUTF("PING|0|0|ping|0");  // header giả để worker nhận
            dos.flush();

            // đọc phản hồi nếu worker chạy
            DataInputStream dis = new DataInputStream(s.getInputStream());
            dis.readUTF();  // worker sẽ trả FAIL hoặc timeout
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    // ============================================================
    // GỬI JOB CONVERT CHO WORKER
    // ============================================================
    public static String sendConvertJob(int type, int userId, String originalFileName, String inputPath) {
        try (
            Socket socket = new Socket(WORKER_HOST, WORKER_PORT);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
        ) {

            File inputFile = new File(inputPath);
            long fileSize = inputFile.length();

            // ===== 1. Gửi HEADER =====
            String header = String.format("CONVERT|%d|%d|%s|%d",
                    type, userId, originalFileName, fileSize);
            dos.writeUTF(header);
            dos.flush();

            // ===== 2. Gửi file =====
            try (FileInputStream fis = new FileInputStream(inputFile)) {
                byte[] buf = new byte[4096];
                int read;
                while ((read = fis.read(buf)) != -1) {
                    dos.write(buf, 0, read);
                }
                dos.flush();
            }

            // ===== 3. Nhận phản hồi =====
            String respHeader = dis.readUTF();
            if (!respHeader.startsWith("FILE|OK|")) {
                return "FAIL|CONVERT_ERROR:" + respHeader; // FAIL|... hoặc lỗi khác
            }

            // tách thông tin file
            String[] parts = respHeader.split("\\|", 4);
            String fileName = parts[2];
            long fileSizetemp = Long.parseLong(parts[3]);

            // tạo file lưu vào downloads/
            File downloadsDir = new File("downloads");
            if (!downloadsDir.exists()) downloadsDir.mkdirs();
            File outFile = new File(downloadsDir, fileName);

            // đọc nhị phân từ socket và lưu vào file
            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                byte[] buf = new byte[8192];
                long remain = fileSizetemp;
                int read;
                while (remain > 0 && (read = dis.read(buf, 0, (int)Math.min(buf.length, remain))) != -1) {
                    fos.write(buf, 0, read);
                    remain -= read;
                }
                fos.flush();
            }
            ConvertToPDFDAO dao = new ConvertToPDFDAO();
            Link l = new Link();
            l.setID(userId);
            l.setType(type == 1);
            l.setLink(outFile.getAbsolutePath());

            if (!dao.saveLink(l)) {
                return "FAIL|DB_SAVE_FAILED";
            }

            return "OK|" + outFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL|WORKER_CONNECTION_ERROR:" + e.getMessage();
        }
    }
}
