package controller;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import client.SocketClient;
import model.BEAN.Link;


@WebServlet("/CheckLoginServlet")
public class CheckLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final SocketClient socketClient = new SocketClient("26.241.40.229", 8088);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("user");
        String password = request.getParameter("pass");

        HttpSession session = request.getSession();
        try {
            String cmd
            
            = String.format("LOGIN|%s|%s", username, password);
            String resp = socketClient.sendCommand(cmd);
            // expected response: OK|<userID>  or FAIL|message
            if (resp != null && resp.startsWith("OK|")) {
                // fallback parsing
                String userIdStr = resp.substring(3);
                int userId = Integer.parseInt(userIdStr.trim());
                session.removeAttribute("error");
                session.setAttribute("userID", userId);

                // request user's data list
                String dataResp = socketClient.sendCommand("GETDATA|" + userId);
                Vector<Link> list = new Vector<>();
                if (dataResp != null && dataResp.startsWith("LINKS|")) {
                    String payload = dataResp.substring(6); // after LINKS|
                    if (!payload.trim().isEmpty()) {
                        String[] items = payload.split(";");
                        for (String item : items) {
                            // expected each item: id,type,link
                            String[] f = item.split(",", 3);
                            if (f.length >= 3) {
                                try {
                                    int id = Integer.parseInt(f[0].trim());
                                    boolean type = "1".equals(f[1].trim()) || "true".equalsIgnoreCase(f[1].trim());
                                    String link = f[2].trim();
                                    list.add(new Link(id, link, type));
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        }
                    }
                }
                session.setAttribute("links", list);
                response.sendRedirect("Home.jsp");
                System.out.println("Session userID: " + request.getSession().getAttribute("userID"));
            } else {
                String msg = (resp == null) ? "Server không phản hồi" : resp;
                session.setAttribute("error", "Tài khoản hoặc mật khẩu không đúng! " + msg);
                response.sendRedirect("Login.jsp");
            }
        } catch (Exception e) {
            session.setAttribute("error", "Lỗi kết nối tới server: " + e.getMessage());
            response.sendRedirect("Login.jsp");
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("Login.jsp");
    }
}
