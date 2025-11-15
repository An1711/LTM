package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.BEAN.Account;
import model.BO.CheckLoginBO;
import model.DAO.CheckLoginDAO;

@WebServlet("/CheckLoginServlet")
public class CheckLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

   

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("user");
        String password = request.getParameter("pass");

        Account acc = CheckLoginBO.checkLogin(username, password);

        HttpSession session = request.getSession();

        if (acc != null) {
            session.removeAttribute("error"); 
            response.sendRedirect("Home.jsp");

        } else {
            session.setAttribute("error", "Tài khoản hoặc mật khẩu không đúng!");
            response.sendRedirect("Login.jsp");  
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("Login.jsp");
    }
}
