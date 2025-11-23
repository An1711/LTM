<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	import="model.BEAN.Link"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>File Converter</title>
    <link rel="stylesheet" href="Convert.css">
    <style>
        /* Thêm style cho khu vực kết quả */
        .result-section {
            text-align: center;
            margin-top: 30px;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 8px;
            background-color: #f9f9f9;
            max-width: 600px;
            margin-left: auto;
            margin-right: auto;
        }
        .success-message { color: #28a745; }
        .error-message { color: #dc3545; }
        .download-link {
            display: inline-block;
            margin-top: 15px;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }
    </style>
</head>
<body>
    <main class="main-container">
        <header class="header-section">
            <div class="header-content">
                <h1 class="main-title">Convert Word to PDF Instantly</h1>
                <p class="main-description">
                    Select your .doc or .docx file and we'll convert it to PDF for you.
                </p>
            </div>
        </header>

        <section class="upload-section">
            <div class="upload-container">
                <div class="upload-wrapper">
                    <div class="upload-zone">
                        <div class="upload-content">
                            <img
                                src="https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/e23b0b10cd1d608ca66ee0ba0468254eee2d3178?placeholderIfAbsent=true"
                                alt="Upload file icon"
                                class="upload-icon"
                            />
                            <div class="upload-text-container">
                                <div class="upload-text-content">
                                    <h2 class="upload-title">Drop your file here</h2>
                                    <p class="upload-subtitle">or click to browse from your computer</p>
                                    <form action="ConvertToPDFServlet" method="post" enctype="multipart/form-data">
                                        <input type="file" id="fileInput" name="fileUpload" hidden>
                                        <button type="button" class="upload-button" onclick="document.getElementById('fileInput').click();">
                                            <img    
                                                src="https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/d649be96b91626415cc041f36b32f0e5bc3bd2dd?placeholderIfAbsent=true"
                                                alt="Upload icon"
                                                class="button-icon"
                                            />
                                            <span class="button-text">Select File</span>
                                        </button>
                                        <script>
                                            document.getElementById("fileInput").addEventListener("change", function () {
                                                this.form.submit();
                                            });
                                        </script>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- KHU VỰC HIỂN THỊ KẾT QUẢ SỬ DỤNG SCRIPTLET -->
        <%
            // Lấy các attribute từ request mà servlet đã gửi qua
            String downloadLink = (String) request.getAttribute("downloadLink");
            String errorMessage = (String) request.getAttribute("errorMessage");
        
            // Chỉ hiển thị khu vực này nếu có kết quả (thành công hoặc lỗi)
            if (downloadLink != null || errorMessage != null) {
        %>
            <section class="result-section">
                <%-- Hiển thị kết quả thành công --%>
                <% if (downloadLink != null) { %>
                    <h2 class="success-message">Conversion Successful!</h2>
                    <p>Your PDF file is ready to be downloaded.</p>
                    <a href="<%= downloadLink %>" class="download-link" download>Download PDF</a>
                <% } %>

                <%-- Hiển thị thông báo lỗi --%>
                <% if (errorMessage != null) { %>
                    <h2 class="error-message">Conversion Failed</h2>
                    <p><%= errorMessage %></p>
                <% } %>
            </section>
        <%
            } 
        %>

        <form action="BackServlet" method="get" style="text-align: center; margin-top: 20px;">
            <button class="back-button">BACK</button>
        </form>
    </main>
</body>
</html>