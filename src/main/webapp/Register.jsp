<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Đăng ký tài khoản</title>
<link rel="stylesheet" href="Login.css">
</head>
<body>
    <div class="login-container">
        <h1>Đăng ký</h1>
        <form action="RegisterServlet" method="post">
            <input type="text" name="username" placeholder="Tên đăng nhập" required>
            <input type="password" name="password" placeholder="Mật khẩu" required>
            <input type="password" name="confirmPassword" placeholder="Xác nhận mật khẩu" required>
            <input type="submit" value="Tạo tài khoản">
        </form>

        <div class="signup-link">
            Đã có tài khoản?
            <a href="login.jsp">Đăng nhập</a>
        </div>
    </div>
</body>
</html>
