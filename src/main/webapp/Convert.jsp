<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>File Converter</title>
    <link rel="stylesheet" href="Convert.css">
</head>
<body>
    <main class="main-container">
        <header class="header-section">
            <div class="header-content">
                <h1 class="main-title">Convert Your Files Instantly</h1>
                <p class="main-description">
                    Transform your documents, images, and media files with our powerful
                    conversion tool. Fast, secure, and easy to use.
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
                                    <button class="upload-button" onclick="handleFileSelect()">
                                        <img
                                            src="https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/d649be96b91626415cc041f36b32f0e5bc3bd2dd?placeholderIfAbsent=true"
                                            alt="Upload icon"
                                            class="button-icon"
                                        />
                                        <span class="button-text">Select File</span>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </main>

    <script>
        function handleFileSelect() {
            console.log("File selection triggered");
            // File selection logic would go here
        }
    </script>
</body>
</html>
