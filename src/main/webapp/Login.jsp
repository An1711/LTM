<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login</title>
<link rel=stylesheet href="Login.css">
</head>
<body>
  <div class="login-container">
    <h1>Đăng nhập</h1>
    <%
    String err = (String) session.getAttribute("error");
    if (err != null) {
%>
        <p style="color:red;"><%= err %></p>
<%
        session.removeAttribute("error"); 
    }
%>

    
    <form action="CheckLoginServlet" method="post">
        <input name="user" type="text" placeholder="Tài Khoản">
        <input name="pass" type="password" placeholder="Mật Khẩu">
        <input value="Đăng nhập" type="submit">
    </form>
    <div class="signup-link">
    Bạn chưa có tài khoản?
    <form action="RegisterServlet" method="get" style="display:inline;">
        <input type="submit" value="Đăng ký" style="background:none;border:none;color:blue;text-decoration:underline;cursor:pointer;">
    </form>
</div>
  </div>
</body>
</html>