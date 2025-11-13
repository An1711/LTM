package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.DAO.RegisterDAO;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private RegisterDAO registerDAO = new RegisterDAO();

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

       
        if (registerDAO.isUsernameExists(username)) {
            request.setAttribute("error", "Tên đăng nhập đã tồn tại, hãy chọn tên khác!");
            request.getRequestDispatcher("Register.jsp").forward(request, response);
            return;
        }

       
        boolean ok = registerDAO.register(username, password);
        if (ok) {
           
            request.setAttribute("message", "Tạo tài khoản thành công, hãy đăng nhập!");
            request.getRequestDispatcher("Login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Có lỗi xảy ra, vui lòng thử lại!");
            request.getRequestDispatcher("Register.jsp").forward(request, response);
        }
    }
}
