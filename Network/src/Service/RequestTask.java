package Service;

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
        try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            String resp = ServerService.processCommand(message);
            writer.println(resp);
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
