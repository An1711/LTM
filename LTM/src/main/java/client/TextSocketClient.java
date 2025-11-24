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
        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(soTimeoutMs);
            OutputStream out = socket.getOutputStream();
            out.write((command + "\n").getBytes(StandardCharsets.UTF_8));
            out.flush();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            return reader.readLine();

        } catch (SocketTimeoutException ste) {
            throw new Exception("Timeout waiting for server");
        }
    }
}
