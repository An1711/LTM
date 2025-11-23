package controller;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.IOException;

import client.FileSocketClient;

@WebServlet("/ConvertToPDFServlet")
@MultipartConfig
public class ConvertToPDFServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ConvertToPDFServlet() { super(); }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    		
            throws ServletException, IOException {
    	System.out.println(">>>> ConvertToPDFServlet START");

        request.setCharacterEncoding("UTF-8");
        Part filePart = request.getPart("fileUpload");
        String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String appPath = request.getServletContext().getRealPath("");
        String downloadPath = appPath + File.separator + "downloads";
        new File(downloadPath).mkdirs();

        int userID = (int) request.getSession().getAttribute("userID");
        int type = Integer.parseInt((String) request.getSession().getAttribute("type"));

        // save uploaded file to temp upload dir first
        File uploads = new File(appPath, "uploads");
        if (!uploads.exists()) uploads.mkdirs();
        File temp = new File(uploads, System.currentTimeMillis() + "_" + originalFileName);
        try (InputStream in = filePart.getInputStream()) {
            Files.copy(in, temp.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }

        FileSocketClient fileClient = new FileSocketClient("26.241.40.229", 8088, 10000);
        try {
            String result = fileClient.uploadFile(temp, type, userID, new File(downloadPath));
            if (result != null && result.startsWith("OK|")) {
                // result = OK|absoluteLocalPath
                String localPath = result.substring(3);
                String fileName = new File(localPath).getName();
                request.setAttribute("downloadLink", "downloads/" + fileName);
            } else {
                request.setAttribute("errorMessage", result == null ? "No response" : result);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Lỗi kết nối: " + e.getMessage());
        } finally {
            temp.delete();
        }

        request.getRequestDispatcher("Convert.jsp").forward(request, response);
    }
}
