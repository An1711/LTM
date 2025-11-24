package controller;

import client.DownloadSocketClient;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/DownloadServlet")
public class DownloadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String key = req.getParameter("key");
        if (key == null || key.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing key");
            return;
        }

        DownloadSocketClient client = new DownloadSocketClient(getServletContext());
        try {
            client.streamDownloadToResponse(key, resp);
        } catch (Exception e) {
            resp.setContentType("text/plain; charset=UTF-8");
            resp.getWriter().write("FAIL|" + e.getMessage());
        }
    }
}
