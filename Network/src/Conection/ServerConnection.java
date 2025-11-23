package Conection;

import java.io.IOException;
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
            System.out.println("[Server] Listening at port: " + port);
        } catch (IOException e) {
            System.err.println("[Server] Cannot start: " + e.getMessage());
        }

        // ThreadPool:
        // 4 worker threads
        // Queue size = 10
        pool = new ThreadPoolExecutor(
                4,     // core
                4,     // max
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(10)
        );
    }

    public void start() {
        while (true) {
            try {
                // Accept new client
                Socket socket = server.accept();
                System.out.println("[Server] Client connected: " + socket.getRemoteSocketAddress());

                // IMPORTANT:
                // Không đọc bất kỳ byte nào ở đây!
                // Không dùng BufferedReader, PrintWriter, readLine().
                // Giao hết socket cho RequestTask để xử lý chuẩn giao thức.

                RequestTask task = new RequestTask(socket);
                try {
                    pool.execute(task);
                } catch (RejectedExecutionException rx) {
                    System.out.println("[Server] Queue full — rejecting client");

                    try {
                        socket.getOutputStream().write("FAIL|QUEUE_FULL\n".getBytes("UTF-8"));
                        socket.getOutputStream().flush();
                    } catch (Exception ignore) {}

                    try { socket.close(); } catch (Exception ignore) {}
                }

            } catch (IOException e) {
                System.err.println("[Server] Accept error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        ServerConnection server = new ServerConnection(8088);
        server.start();
    }
}
