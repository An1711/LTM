package controller;

import java.io.IOException;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import client.TextSocketClient;
import model.BEAN.Link;

@WebServlet("/CheckLoginServlet")
public class CheckLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public CheckLoginServlet() { super(); }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        String username = request.getParameter("user");
        String password = request.getParameter("pass");
        TextSocketClient socketClient = new TextSocketClient(getServletContext());


        try {
            String cmd = String.format("LOGIN|%s|%s", username, password);
            String resp = socketClient.sendCommand(cmd);
            
				       

            
            if (resp != null && resp.startsWith("OK|")) {
                int userId = Integer.parseInt(resp.substring(3).trim());
                session.removeAttribute("error");
                session.setAttribute("userID", userId);

             // get data từ server
                String dataResp = socketClient.sendCommand("GETDATA|" + userId);
                			
                			

                
                Vector<Link> list = new Vector<>();

                if (dataResp != null && dataResp.startsWith("DATA|")) {

                    String[] lines = dataResp.split("\n");

                    for (int i = 1; i < lines.length; i++) {
                        String line = lines[i].trim();

                        if (line.equals("END")) break;
                        if (line.isEmpty()) continue;

                        String[] f = line.split(",", 3);
                        if (f.length == 3) {
                            try {
                                int id = Integer.parseInt(f[0].trim());
                                boolean type = f[1].trim().equals("1");
                                String link = f[2].trim();
                                list.add(new Link(id, link, type));
                       
                            } catch (NumberFormatException e) {}
                        }
                    }
                }

                session.setAttribute("links", list);
                response.sendRedirect(request.getContextPath() + "/Home.jsp");
                return;
            } else {
                String msg = (resp == null) ? "Server không phản hồi" : "Tài khoản hoặc mật khẩu không đúng";
                session.setAttribute("error", msg);
                response.sendRedirect(request.getContextPath() + "/Login.jsp");
                return;
            }
        } catch (Exception e) {
            session.setAttribute("error", "Lỗi kết nối tới server: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/Login.jsp");
    }
}
