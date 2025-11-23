package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import client.SocketClient;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final SocketClient socketClient = new SocketClient("26.241.40.229", 8088);

    public RegisterServlet() {
        super();
    }

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chỉ đơn giản hiển thị form đăng ký
        request.getRequestDispatcher("Register.jsp").forward(request, response);
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirm  = request.getParameter("confirmPassword");

        
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || confirm == null  || confirm.trim().isEmpty()) {

            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin!");
            request.getRequestDispatcher("Register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirm)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp!");
            request.getRequestDispatcher("Register.jsp").forward(request, response);
            return;
        }

       
        try {
            // send register command to server: REGISTER|username|password
            String cmd = String.format("REGISTER|%s|%s", username, password);
            String resp = socketClient.sendCommand(cmd);
            if (resp != null && resp.startsWith("OK")) {
                request.setAttribute("message", "Tạo tài khoản thành công, hãy đăng nhập!");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
            } else {
                String err = (resp == null) ? "Server không phản hồi" : resp;
                request.setAttribute("error", "Có lỗi: " + err);
                request.getRequestDispatcher("Register.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi kết nối tới server: " + e.getMessage());
            request.getRequestDispatcher("Register.jsp").forward(request, response);
        }
    }
}
