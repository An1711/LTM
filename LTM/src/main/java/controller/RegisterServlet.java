package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import client.TextSocketClient;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8088;

    public RegisterServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("Register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirm  = request.getParameter("confirmPassword");

        // Validation input
        if (isEmpty(username) || isEmpty(password) || isEmpty(confirm)) {
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
            // Create a text client for this request
            TextSocketClient client = new TextSocketClient(SERVER_HOST, SERVER_PORT);

            // Build command for server socket
            String command = "REGISTER|" + username + "|" + password;

            // Send and receive one-line text response
            String resp = client.sendCommand(command);

            if (resp != null && resp.startsWith("OK")) {
                request.setAttribute("message", "Tạo tài khoản thành công! Hãy đăng nhập.");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
            } else {
                request.setAttribute("error", resp == null ? "Server không phản hồi." : resp);
                request.getRequestDispatcher("Register.jsp").forward(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("error", "Lỗi kết nối tới server: " + e.getMessage());
            request.getRequestDispatcher("Register.jsp").forward(request, response);
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
