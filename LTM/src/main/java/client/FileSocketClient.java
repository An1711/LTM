package client;

import config.AppConfig;
import javax.servlet.ServletContext;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FileSocketClient {

    private final String host;
    private final int port;
    private final int soTimeoutMs;

    // Tự đọc host/port từ web.xml
    public FileSocketClient(ServletContext ctx) {
        this.host = AppConfig.getServerHost(ctx);
        this.port = AppConfig.getServerPort(ctx);
        this.soTimeoutMs = 5000;
    }

    public FileSocketClient(ServletContext ctx, int soTimeoutMs) {
        this.host = AppConfig.getServerHost(ctx);
        this.port = AppConfig.getServerPort(ctx);
        this.soTimeoutMs = soTimeoutMs;
    }

    public String uploadFile(File localFile, int type, int userId, File downloadDir) throws Exception {
        try (Socket socket = new Socket(host, port)) {
            if (soTimeoutMs > 0) socket.setSoTimeout(soTimeoutMs);
            OutputStream out = new BufferedOutputStream(socket.getOutputStream());
            InputStream in = new BufferedInputStream(socket.getInputStream());

            long fileSize = localFile.length();
            String header = String.format("UPLOAD|%d|%d|%s|%d\n",
                    type, userId, localFile.getName(), fileSize);

            out.write(header.getBytes(StandardCharsets.UTF_8));
            out.flush();

            // Send bytes
            try (FileInputStream fis = new FileInputStream(localFile)) {
                byte[] buf = new byte[8192];
                int r;
                while ((r = fis.read(buf)) != -1) {
                    out.write(buf, 0, r);
                }
                out.flush();
            }

            // Read response
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String resp = reader.readLine();
            if (resp == null) throw new Exception("No response from server");

            if (resp.startsWith("FILE|OK|")) {
                String[] parts = resp.split("\\|", 4);
                String serverFileName = parts[2];
                long returnedSize = Long.parseLong(parts[3]);

                File outFile = new File(downloadDir, serverFileName);
                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    byte[] buf = new byte[8192];
                    long remaining = returnedSize;
                    while (remaining > 0) {
                        int n = in.read(buf, 0, (int)Math.min(buf.length, remaining));
                        if (n == -1) break;
                        fos.write(buf, 0, n);
                        remaining -= n;
                    }
                }
                return "OK|" + outFile.getAbsolutePath();
            }

            return resp;
        }
    }
}
