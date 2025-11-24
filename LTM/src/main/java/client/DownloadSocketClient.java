package client;

import config.AppConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletOutputStream;

public class DownloadSocketClient {

    private final String host;
    private final int port;
    private final int soTimeoutMs = 10000;

    public DownloadSocketClient(ServletContext ctx) {
    	this.host = AppConfig.getServerHost(ctx);
        this.port = AppConfig.getServerPort(ctx);
    }

    /**
     * Send DOWNLOAD|key to server and stream returned file to given HttpServletResponse.
     * This method will set response headers (Content-Disposition, Content-Length) before streaming.
     */
    public void streamDownloadToResponse(String key, HttpServletResponse resp) throws Exception {
        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(soTimeoutMs);

            OutputStream out = new BufferedOutputStream(socket.getOutputStream());
            InputStream in = new BufferedInputStream(socket.getInputStream());

            String cmd = "DOWNLOAD|" + key + "\n";
            out.write(cmd.getBytes(StandardCharsets.UTF_8));
            out.flush();

            // Read response header line
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String header = reader.readLine();
            if (header == null) throw new Exception("No response from server");

            if (!header.startsWith("FILE|OK|")) {
                // server returned an error line like FAIL|NOT_FOUND
                throw new Exception(header);
            }

            String[] parts = header.split("\\|", 4);
            String fileName = parts.length > 2 ? parts[2] : "download.bin";
            long size = 0;
            if (parts.length > 3) {
                try { size = Long.parseLong(parts[3]); } catch (Exception ignore) {}
            }

            resp.setContentType("application/octet-stream");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            if (size > 0) resp.setHeader("Content-Length", String.valueOf(size));

            // Now stream binary from socket input to servlet output
            ServletOutputStream sos = resp.getOutputStream();
            byte[] buf = new byte[8192];
            long remaining = size;

            // If size known, read exact bytes; otherwise read until socket closes
            if (remaining > 0) {
                while (remaining > 0) {
                    int toRead = (int) Math.min(buf.length, remaining);
                    int n = in.read(buf, 0, toRead);
                    if (n == -1) break;
                    sos.write(buf, 0, n);
                    remaining -= n;
                }
            } else {
                int n;
                while ((n = in.read(buf)) != -1) {
                    sos.write(buf, 0, n);
                }
            }

            sos.flush();
        }
    }
}
