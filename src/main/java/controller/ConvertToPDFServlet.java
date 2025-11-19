// ConvertToPDFServlet.java - PHIÊN BẢN ĐÃ SỬA

package controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import model.BO.ConvertToPDFBO;

@WebServlet("/ConvertToPDFServlet")
@MultipartConfig
public class ConvertToPDFServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ConvertToPDFBO convertToPDFBO;

    public void init() {
        convertToPDFBO = new ConvertToPDFBO();
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
        try (InputStream input = filePart.getInputStream()) {
        	if(type == 1) {
            Files.copy(input, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            downloadLink = convertToPDFBO.convertDocToPdfAndSave(tempFilePath.toString(), originalFileName, downloadPath, userID);
        	}
        	else {
        		Files.copy(input, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
                downloadLink = convertToPDFBO.convertPdfToDocAndSave(tempFilePath.toString(), originalFileName, downloadPath, userID);
        	}
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi trong quá trình xử lý file.");
        } finally {
            // Xóa file tạm sau khi đã xử lý xong (dù thành công hay thất bại)
            try {
                Files.deleteIfExists(tempFilePath);
            } catch (IOException e) {
                e.printStackTrace(); // Ghi log lỗi xóa file tạm nếu có
            }
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