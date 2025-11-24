package client;

import config.AppConfig;
import javax.servlet.ServletContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class TextSocketClient {

    private final String host;
    private final int port;
    private final int soTimeoutMs;

    public TextSocketClient(ServletContext ctx) {
        this.host = AppConfig.getServerHost(ctx);
        this.port = AppConfig.getServerPort(ctx);
        this.soTimeoutMs = 5000;
    }

    public TextSocketClient(ServletContext ctx, int timeout) {
        this.host = AppConfig.getServerHost(ctx);
        this.port = AppConfig.getServerPort(ctx);
        this.soTimeoutMs = timeout;
    }

    public String sendCommand(String command) throws Exception {

      //  System.out.println("[CLIENT_SOCKET] sendCommand = " + command);

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(soTimeoutMs);

            OutputStream out = socket.getOutputStream();
            out.write((command + "\n").getBytes(StandardCharsets.UTF_8));
            out.flush();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {

                sb.append(line).append("\n");
                System.out.println("[CLIENT_SOCKET] recv = " + line);

                // === Điều kiện dừng ===
                if (line.startsWith("OK|") || line.startsWith("FAIL|")) {
                    break; // lệnh 1-line
                }

                if (line.equals("END")) {
                    break; // dữ liệu GETDATA kết thúc
                }
            }

            String resp = sb.toString();
            System.out.println("[CLIENT_SOCKET] respRAW:\n" + resp);

            return resp;

        } catch (SocketTimeoutException ste) {
            throw new Exception("Timeout waiting for server");
        }
    }

}
