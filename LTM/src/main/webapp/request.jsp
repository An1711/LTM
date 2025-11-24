<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Requests</title>
    <link rel="stylesheet" href="Home.css">
</head>
<body>
<div class="frame">
    <div class="body">
        <header class="header">
            <div class="header-inner">
                <h1>My Conversion Requests</h1>
            </div>
        </header>

        <main class="main-content">
            <div style="padding: 20px;">
                <%
                    List<String[]> reqs = (List<String[]>) request.getAttribute("requests");
                    if (reqs == null || reqs.isEmpty()) {
                %>
                    <p>No requests found.</p>
                <%
                    } else {
                %>
                    <table style="width:100%; border-collapse: collapse;">
                        <tr>
                            <th style="border-bottom:1px solid #ccc; padding:8px">ID</th>
                            <th style="border-bottom:1px solid #ccc; padding:8px">Type</th>
                            <th style="border-bottom:1px solid #ccc; padding:8px">File</th>
                            <th style="border-bottom:1px solid #ccc; padding:8px">Status</th>
                            <th style="border-bottom:1px solid #ccc; padding:8px">Result</th>
                        </tr>
                        <%
                        for (String[] r : reqs) {
                            String id = r.length>0? r[0] : "";
                            String type = r.length>1? (r[1].equals("1")? "DOC→PDF" : "PDF→DOC") : "";
                            String file = r.length>2? r[2] : "";
                            String status = r.length>3? r[3] : "";
                            String result = r.length>4? r[4] : "";
                        %>
                        <tr>
                            <td style="padding:8px"><%= id %></td>
                            <td style="padding:8px"><%= type %></td>
                            <td style="padding:8px"><%= file %></td>
                            <td style="padding:8px"><%= status %></td>
                            <td style="padding:8px;"><%= result %></td>
                        </tr>
                        <%
                        }
                        %>
                    </table>
                <%
                    }
                %>
                <p><a href="Home">Back to Home</a></p>
            </div>
        </main>
    </div>
</div>
</body>
</html>
