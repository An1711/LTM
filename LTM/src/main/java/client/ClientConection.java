package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConection {
	public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8088);
            System.out.println("Đã kết nối tới server!");

            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Gửi dữ liệu
            writer.println("Hello Server");

            // Nhận phản hồi
            String response = reader.readLine();
            System.out.println("Phản hồi từ server: " + response);

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
