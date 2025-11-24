package controller;

import client.TextSocketClient;
import model.BEAN.Link;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Vector;

@WebServlet("/Home")
public class GetDataServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userID");
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        try {
            TextSocketClient client = new TextSocketClient(getServletContext());
            String dataResp = client.sendCommand("GETDATA|" + userId);

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
                        } catch (NumberFormatException ignore) {}
                    }
                }
            }

            session.setAttribute("links", list);
            request.getRequestDispatcher("/Home.jsp").forward(request, response);

        } catch (Exception e) {
            session.setAttribute("error", "Lỗi lấy dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/Home.jsp").forward(request, response);
        }
    }
}
