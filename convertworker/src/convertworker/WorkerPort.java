package convertworker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WorkerPort {

    private static final String DOWNLOAD_DIR = "downloads";

    public static void main(String[] args) throws Exception {
        File d = new File(DOWNLOAD_DIR);
        if (!d.exists()) d.mkdirs();

        ServerSocket server = new ServerSocket(9999);
        System.out.println("Worker đang chạy tại cổng 9999...");

        while (true) {
            Socket client = server.accept();
            new WorkerBrain(client).start();
        }
    }
}
