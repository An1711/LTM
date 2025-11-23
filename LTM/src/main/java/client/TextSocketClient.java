package client;

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

    public TextSocketClient(String host, int port) {
        this(host, port, 5000);
    }

    public TextSocketClient(String host, int port, int soTimeoutMs) {
        this.host = host;
        this.port = port;
        this.soTimeoutMs = soTimeoutMs;
    }

    /**
     * Send one-line command (ending with newline) and read one-line response (up to newline).
     */
    public String sendCommand(String command) throws Exception {
        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(soTimeoutMs);
            OutputStream out = socket.getOutputStream();
            // send header line
            out.write((command + "\n").getBytes(StandardCharsets.UTF_8));
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            String line = reader.readLine(); // read one-line response
            return line;
        } catch (SocketTimeoutException ste) {
            throw new Exception("Timeout waiting response from server");
        }
    }
}
