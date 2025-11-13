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
    <form action="CheckInfoServlet" method="post">
        <input name="user" type="text" placeholder="Tài Khoản">
        <input name="pass" type="password" placeholder="Mật Khẩu">
        <input value="Đăng nhập" type="submit">
    </form>
    <div class="signup-link">
        Bạn chưa có tài khoản? <a href="register.jsp">Đăng ký</a>
    </div>
  </div>
</body>
</html>