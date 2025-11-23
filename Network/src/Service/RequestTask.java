package Service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class RequestTask implements Runnable {
    private final Socket socket;
    private final String message;

    public RequestTask(Socket socket, String message) {
        this.socket = socket;
        this.message = message;
    }

    @Override
    public void run() {
        try {
            // Handle CONVERT specially: header contains filesize, then raw bytes follow
            if (message != null && message.startsWith("CONVERT|")) {
                // expected: CONVERT|<type>|<userID>|<filename>|<filesize>
                String[] hdr = message.split("\\|", 6);
                if (hdr.length < 5) {
                    try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                        writer.println("FAIL|BAD_FORMAT");
                    }
                    return;
                }

                String typeStr = hdr[1];
                String userIdStr = hdr[2];
                String filename = hdr[3];
                long size = 0L;
                try { size = Long.parseLong(hdr[4]); } catch (Exception e) { size = 0L; }

                // Create uploads dir
                File uploads = new File("uploads");
                if (!uploads.exists()) uploads.mkdirs();

                File temp = new File(uploads, System.currentTimeMillis() + "_" + filename);

                try (InputStream in = new BufferedInputStream(socket.getInputStream());
                     FileOutputStream fos = new FileOutputStream(temp)) {

                    long remaining = size;
                    byte[] buffer = new byte[8192];
                    while (remaining > 0) {
                        int toRead = (int)Math.min(buffer.length, remaining);
                        int r = in.read(buffer, 0, toRead);
                        if (r == -1) break;
                        fos.write(buffer, 0, r);
                        remaining -= r;
                    }
                    fos.flush();
                }

                // process conversion
                int type = "1".equals(typeStr) ? 1 : 0;
                int userId = 0;
                try { userId = Integer.parseInt(userIdStr); } catch (Exception e) {}

                String result = ServerService.processConvert(type, userId, filename, temp.getAbsolutePath());
                try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                    writer.println(result);
                }
                // delete temp input file
                try { temp.delete(); } catch (Exception ignored) {}
                return;
            }

            // default: simple text command
            try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                String resp = ServerService.processCommand(message);
                writer.println(resp);
            }
        } catch (Exception e) {
            try {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println("FAIL|EXCEPTION:" + e.getMessage());
                writer.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try { socket.close(); } catch (Exception ignored) {}
        }
    }
}
