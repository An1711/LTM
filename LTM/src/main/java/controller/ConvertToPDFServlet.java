// ConvertToPDFServlet.java - PHIÊN BẢN ĐÃ SỬA

package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/ConvertToPDFServlet")
@MultipartConfig
public class ConvertToPDFServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public void init() {
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        Part filePart = request.getPart("fileUpload");
        String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

        if (originalFileName == null || originalFileName.isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng chọn một file để chuyển đổi.");
            request.getRequestDispatcher("Convert.jsp").forward(request, response);
            return;
        }

//        if (!originalFileName.toLowerCase().endsWith(".doc") && !originalFileName.toLowerCase().endsWith(".docx")) {
//            request.setAttribute("errorMessage", "Loại file không hợp lệ. Vui lòng chỉ chọn file .doc hoặc .docx.");
//            request.getRequestDispatcher("Convert.jsp").forward(request, response);
//            return;
//        }
        
        // SỬA LẠI ĐƯỜNG DẪN - Lấy đường dẫn thực của ứng dụng web
        String appPath = request.getServletContext().getRealPath("");
        
        // 1. Tạo thư mục UPLOADS để lưu file tạm
        String uploadPath = appPath + File.separator + "uploads";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        // 2. Tạo thư mục DOWNLOADS để lưu file kết quả
        String downloadPath = appPath + File.separator + "downloads";
        File downloadDir = new File(downloadPath);
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }
        
        int userID = (int)request.getSession().getAttribute("userID");
        
        Path tempFilePath = Paths.get(uploadPath + File.separator + originalFileName);
        String downloadLink = null;
        int type = Integer.parseInt((String)request.getSession().getAttribute("type"));

        // Send file and convert request to Network server over socket
        try (InputStream fileInput = filePart.getInputStream();
             Socket socket = new Socket("localhost", 8088);
             OutputStream out = socket.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            long size = filePart.getSize();
            String header = String.format("CONVERT|%d|%d|%s|%d", type, userID, originalFileName, size);
            // send header line
            out.write((header + "\n").getBytes("UTF-8"));
            out.flush();

            // stream file bytes
            byte[] buffer = new byte[8192];
            int r;
            while ((r = fileInput.read(buffer)) != -1) {
                out.write(buffer, 0, r);
            }
            out.flush();

            // read response line
            String resp = reader.readLine();
            if (resp != null && resp.startsWith("OK|")) {
                downloadLink = resp.substring(3);
            } else {
                request.setAttribute("errorMessage", resp == null ? "Không có phản hồi từ server" : resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi trong quá trình gửi dữ liệu tới server: " + e.getMessage());
        }
        
        // Trả kết quả về cho view
        if (downloadLink != null) {
            request.setAttribute("downloadLink", downloadLink);
        } else {
            if (request.getAttribute("errorMessage") == null) {
                request.setAttribute("errorMessage", "Có lỗi xảy ra trong quá trình chuyển đổi file.");
            }
        }
        
        request.getRequestDispatcher("Convert.jsp").forward(request, response);
    }
}