package Conection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import Service.RequestTask;

public class ServerConnection {
    private ServerSocket server;
    private final ThreadPoolExecutor pool;

    public ServerConnection(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Server đang lắng nghe tại cổng: " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // bounded queue size 10 as requested, with 4 worker threads
        this.pool = new ThreadPoolExecutor(
                4, 4,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(10)
        );
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

                RequestTask task = new RequestTask(socket, message);
                try {
                    pool.execute(task);
                } catch (RejectedExecutionException rex) {
                    // queue full: inform client and close socket
                    System.out.println("Queue đầy - từ chối yêu cầu");
                    writer.println("FAIL|QUEUE_FULL");
                    writer.flush();
                    try { socket.close(); } catch (IOException ignored) {}
                }

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
