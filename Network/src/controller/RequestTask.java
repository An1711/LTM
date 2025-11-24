package controller;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RequestTask implements Runnable {

    private final Socket socket;

    public RequestTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedInputStream bin = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream bout = new BufferedOutputStream(socket.getOutputStream())
        ) {

            // ================================================================
            // 1. ĐỌC HEADER (1 dòng)
            // ================================================================
            String header = readLine(bin);
            if (header == null || header.trim().isEmpty()) {
                writeLine(bout, "FAIL|EMPTY_HEADER");
                return;
            }

            System.out.println("[RequestTask] HEADER = " + header);

            // ================================================================
            // 2. PHÂN LOẠI COMMAND
            // ================================================================
            
            if (header.startsWith("REGISTER|")) {
                handleRegister(header, bout);
                return;
            }

            if (header.startsWith("LOGIN|")) {
                handleLogin(header, bout);
                return;
            }

            if (header.startsWith("GETDATA|")) {
                handleGetData(header, bout);
                return;
            }

            if (header.startsWith("UPLOAD|")) {
                handleUpload(header, bin, bout);
                return;
            }

            if (header.startsWith("DOWNLOAD|")) {
                handleDownload(header, bout);
                return;
            }

            writeLine(bout, "FAIL|UNKNOWN_COMMAND");

        } catch (Exception e) {
            System.err.println("[RequestTask] Exception: " + e.getMessage());
            try {
                BufferedOutputStream bout = new BufferedOutputStream(socket.getOutputStream());
                writeLine(bout, "FAIL|EXCEPTION:" + e.getMessage());
                bout.flush();
            } catch (Exception ignore) {}
        } finally {
            try { socket.close(); } catch (Exception ignore) {}
        }
    }

    // ======================================================================
    //  REGISTER|username|password
    // ======================================================================
    private void handleRegister(String header, BufferedOutputStream bout) throws IOException {
        String[] parts = header.split("\\|", 3);

        String user = parts.length > 1 ? parts[1] : "";
        String pass = parts.length > 2 ? parts[2] : "";

        // Gọi ServerService xử lý
        String result = ServerService.handleRegister(user, pass);
        // result phải có dạng: OK|REGISTER_SUCCESS hoặc FAIL|msg

        writeLine(bout, result);
        bout.flush();
    }

    // ======================================================================
    //  LOGIN|username|password
    // ======================================================================
    private void handleLogin(String header, BufferedOutputStream bout) throws IOException {
        String[] parts = header.split("\\|", 3);
        String user = parts.length > 1 ? parts[1] : "";
        String pass = parts.length > 2 ? parts[2] : "";

        String result = ServerService.handleLogin(user, pass);
        writeLine(bout, result);
        bout.flush();
    }

    // ======================================================================
    //  GETDATA|userId
    // ======================================================================
    private void handleGetData(String header, BufferedOutputStream bout) throws IOException {
        String[] parts = header.split("\\|");
        if (parts.length < 2) {
            writeLine(bout, "FAIL|BAD_GETDATA");
            return;
        }

        int userId = Integer.parseInt(parts[1]);
        String payload = ServerService.handleGetData(userId);

        bout.write(payload.getBytes(StandardCharsets.UTF_8));
        bout.flush();
    }

    // ======================================================================
    //  UPLOAD|type|userId|filename|filesize
    // ======================================================================
    private void handleUpload(String header, BufferedInputStream bin, BufferedOutputStream bout) throws Exception {

        String[] parts = header.split("\\|");
        if (parts.length < 5) {
            writeLine(bout, "FAIL|BAD_UPLOAD_HEADER");
            return;
        }

        int type = Integer.parseInt(parts[1]);
        int userId = Integer.parseInt(parts[2]);
        String filename = parts[3];
        long fileSize = Long.parseLong(parts[4]);

        File uploads = new File("uploads");
        if (!uploads.exists()) uploads.mkdirs();

        File temp = new File(uploads, System.currentTimeMillis() + "_" + filename);

        // Nhận file binary
        try (FileOutputStream fos = new FileOutputStream(temp)) {
            long remaining = fileSize;
            byte[] buf = new byte[8192];

            while (remaining > 0) {
                int toRead = (int) Math.min(buf.length, remaining);
                int n = bin.read(buf, 0, toRead);
                if (n == -1) break;
                fos.write(buf, 0, n);
                remaining -= n;
            }
            fos.flush();
        }

        if (temp.length() != fileSize) {
            writeLine(bout, "FAIL|FILE_INCOMPLETE");
            temp.delete();
            return;
        }

        String convertResult = ServerService.processConvert(type, userId, filename, temp.getAbsolutePath());

        if (convertResult == null || !convertResult.startsWith("OK|")) {
            writeLine(bout, convertResult == null ? "FAIL|CONVERT" : convertResult);
            temp.delete();
            return;
        }

        String outPath = convertResult.substring(3).trim();
        File outFile = new File(outPath);

        if (!outFile.exists()) {
            writeLine(bout, "FAIL|OUTPUT_NOT_FOUND");
            temp.delete();
            return;
        }

        long outSize = outFile.length();

        String outHeader = "FILE|OK|" + outFile.getName() + "|" + outSize + "\n";
        bout.write(outHeader.getBytes(StandardCharsets.UTF_8));
        bout.flush();

        try (FileInputStream fis = new FileInputStream(outFile)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = fis.read(buf)) != -1) {
                bout.write(buf, 0, n);
            }
            bout.flush();
        }

        temp.delete();
    }

    // ======================================================================
    //  DOWNLOAD|filename
    // ======================================================================
    private void handleDownload(String header, BufferedOutputStream bout) throws Exception {
        String fileKey = header.substring("DOWNLOAD|".length()).trim();

        String path = ServerService.resolveDownload(fileKey);
        if (path == null) {
            writeLine(bout, "FAIL|NOT_FOUND");
            return;
        }

        File f = new File(path);
        if (!f.exists()) {
            writeLine(bout, "FAIL|NOT_FOUND");
            return;
        }

        String outHeader = "FILE|OK|" + f.getName() + "|" + f.length() + "\n";
        bout.write(outHeader.getBytes(StandardCharsets.UTF_8));
        bout.flush();

        try (FileInputStream fis = new FileInputStream(f)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = fis.read(buf)) != -1) {
                bout.write(buf, 0, n);
            }
            bout.flush();
        }
    }

    // ======================================================================
    //  HÀM HELPER
    // ======================================================================
    private String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int c;
        while ((c = in.read()) != -1) {
            if (c == '\n') break;
            baos.write(c);
        }
        if (baos.size() == 0 && c == -1) return null;
        return baos.toString(StandardCharsets.UTF_8);
    }

    private void writeLine(OutputStream out, String line) throws IOException {
        out.write((line + "\n").getBytes(StandardCharsets.UTF_8));
    }
}
