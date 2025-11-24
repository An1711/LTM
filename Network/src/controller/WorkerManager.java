package controller;

import java.io.*;
import java.net.Socket;

import model.BO.ConvertToPDFBO;

public class WorkerManager {

    private static final String WORKER_HOST = "localhost";
    private static final int WORKER_PORT = 9999;

    // ============================================================
    // KIỂM TRA WORKER ONLINE
    // ============================================================
    public static boolean isWorkerOnline() {
        try (Socket s = new Socket(WORKER_HOST, WORKER_PORT)) {
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            dos.writeUTF("PING|0|0|ping|0");
            dos.flush();

            DataInputStream dis = new DataInputStream(s.getInputStream());
            dis.readUTF(); 
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

            // ===== 3. Nhận phản hồi HEADER =====
            String respHeader = dis.readUTF();
            if (!respHeader.startsWith("FILE|OK|")) {
                return "FAIL|CONVERT_ERROR:" + respHeader;
            }

            String[] parts = respHeader.split("\\|", 4);
            String fileName = parts[2];
            long fileSizeTemp = Long.parseLong(parts[3]);

            // ===== 4. Lưu file nhận được vào downloads/ =====
            File downloadsDir = new File("downloads");
            if (!downloadsDir.exists()) downloadsDir.mkdirs();
            File outFile = new File(downloadsDir, fileName);

            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                byte[] buf = new byte[8192];
                long remain = fileSizeTemp;
                int read;
                while (remain > 0 &&
                       (read = dis.read(buf, 0, (int)Math.min(buf.length, remain))) != -1) {
                    fos.write(buf, 0, read);
                    remain -= read;
                }
                fos.flush();
            }

            // ===================================================
            // 5. GỌI BO để lưu link vào DB (BO sẽ gọi DAO)
            // ===================================================
            ConvertToPDFBO bo = new ConvertToPDFBO();
            boolean saved = bo.saveConvertedLink(type, userId, outFile.getAbsolutePath());

            if (!saved) {
                return "FAIL|DB_SAVE_FAILED";
            }

            return "OK|" + outFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL|WORKER_CONNECTION_ERROR:" + e.getMessage();
        }
    }
}
