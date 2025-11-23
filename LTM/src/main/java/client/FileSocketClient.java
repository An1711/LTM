package client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FileSocketClient {
    private final String host;
    private final int port;
    private final int soTimeoutMs;

    public FileSocketClient(String host, int port) {
        this(host, port, 0);
    }

    public FileSocketClient(String host, int port, int soTimeoutMs) {
        this.host = host;
        this.port = port;
        this.soTimeoutMs = soTimeoutMs;
    }

    /**
     * Upload a file to server using protocol:
     * header: UPLOAD|type|userID|filename|filesize\n
     * then raw file bytes (filesize)
     *
     * Returns server single-line response (could be FILE|OK|name|size or FAIL|msg).
     * If server responds with FILE|OK... followed by binary, we will read the header and if header indicates file,
     * we will save the returned binary into outFile and return "OK|<path-on-server>" or similar per your design.
     */
    public String uploadFile(File localFile, int type, int userId, File downloadDir) throws Exception {
        try (Socket socket = new Socket(host, port)) {
            if (soTimeoutMs > 0) socket.setSoTimeout(soTimeoutMs);
            OutputStream out = new BufferedOutputStream(socket.getOutputStream());
            InputStream in = new BufferedInputStream(socket.getInputStream());

            long fileSize = localFile.length();
            String header = String.format("UPLOAD|%d|%d|%s|%d\n", type, userId, localFile.getName(), fileSize);
            out.write(header.getBytes(StandardCharsets.UTF_8));
            out.flush();

            // send file bytes
            try (FileInputStream fis = new FileInputStream(localFile)) {
                byte[] buf = new byte[8192];
                int r;
                while ((r = fis.read(buf)) != -1) {
                    out.write(buf, 0, r);
                }
                out.flush();
            }

            // read response header line from server
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String resp = reader.readLine();
            if (resp == null) throw new Exception("No response from server");

            // If server responds with FILE|OK|name|size then read binary
            if (resp.startsWith("FILE|OK|")) {
                String[] parts = resp.split("\\|", 4);
                if (parts.length >= 4) {
                    String serverFileName = parts[2];
                    long returnedSize = Long.parseLong(parts[3]);
                    File outFile = new File(downloadDir, serverFileName);
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        byte[] buf = new byte[8192];
                        long remaining = returnedSize;
                        while (remaining > 0) {
                            int toRead = (int)Math.min(buf.length, remaining);
                            int n = in.read(buf, 0, toRead);
                            if (n == -1) break;
                            fos.write(buf, 0, n);
                            remaining -= n;
                        }
                        fos.flush();
                    }
                    return "OK|" + outFile.getAbsolutePath();
                } else {
                    return "FAIL|INVALID_FILE_HEADER";
                }
            } else {
                // FAIL|... or OK|... text
                return resp;
            }
        }
    }

    /**
     * Download a file by filename or id using protocol:
     * DOWNLOAD|fileIdOrName\n
     */
    public String downloadFile(String fileIdOrName, File downloadDir) throws Exception {
        try (Socket socket = new Socket(host, port)) {
            if (soTimeoutMs > 0) socket.setSoTimeout(soTimeoutMs);
            OutputStream out = socket.getOutputStream();
            InputStream in = new BufferedInputStream(socket.getInputStream());

            String header = String.format("DOWNLOAD|%s\n", fileIdOrName);
            out.write(header.getBytes(StandardCharsets.UTF_8));
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String resp = reader.readLine();
            if (resp == null) throw new Exception("No response from server");

            if (resp.startsWith("FILE|OK|")) {
                String[] parts = resp.split("\\|", 4);
                String fileName = parts[2];
                long size = Long.parseLong(parts[3]);
                File outFile = new File(downloadDir, fileName);
                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    byte[] buf = new byte[8192];
                    long remaining = size;
                    while (remaining > 0) {
                        int toRead = (int) Math.min(buf.length, remaining);
                        int n = in.read(buf, 0, toRead);
                        if (n == -1) break;
                        fos.write(buf, 0, n);
                        remaining -= n;
                    }
                    fos.flush();
                }
                return "OK|" + outFile.getAbsolutePath();
            } else {
                return resp;
            }
        }
    }
}
