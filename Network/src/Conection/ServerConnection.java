package Conection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnection {
	private ServerSocket server;

    public ServerConnection(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Server đang lắng nghe tại cổng: " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        while (true) {
            try {
                // Chờ client kết nối
                Socket socket = server.accept();
                System.out.println("Client đã kết nối: " + socket.getInetAddress());

                // Đọc dữ liệu từ client
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                String message = reader.readLine();
                System.out.println("Nhận từ client: " + message);

                // Trả lời client
                writer.println("Server đã nhận: " + message);

                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Chạy server
    public static void main(String[] args) {
    	ServerConnection server = new ServerConnection(8088);
        server.start();
    }
}
