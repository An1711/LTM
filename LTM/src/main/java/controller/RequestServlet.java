package controller;

import client.TextSocketClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/RequestServlet")
public class RequestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId = (Integer) request.getSession().getAttribute("userID");
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        try {
            TextSocketClient client = new TextSocketClient(getServletContext());
            String resp = client.sendCommand("GETREQS|" + userId);

            List<String[]> list = new ArrayList<>();
            if (resp != null && resp.startsWith("REQS|")) {
                String[] lines = resp.split("\n");
                for (int i = 1; i < lines.length; i++) {
                    String line = lines[i].trim();
                    if (line.equals("END")) break;
                    if (line.isEmpty()) continue;
                    String[] f = line.split(",", 5);
                    list.add(f);
                }
            }

            request.setAttribute("requests", list);
            request.getRequestDispatcher("request.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "Lỗi khi lấy danh sách yêu cầu: " + e.getMessage());
            request.getRequestDispatcher("request.jsp").forward(request, response);
        }
    }
}
