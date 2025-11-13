<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DocConverter</title>
    <link rel="stylesheet" href="Home.css">
</head>
<body>
    <%
        // JSP scriptlet for server-side logic
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String appName = "DocConverter";
        String appVersion = "1.0";
    %>

    <div class="frame">
        <div class="body">
            <header class="header">
                <div class="header-content">
                    <div class="header-inner">
                        <div class="logo-section">
                            <img
                                src="https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/58e4e1cb51aa4f8692238615425fd9d3002aaa7a?placeholderIfAbsent=true"
                                alt="<%= appName %> logo"
                                class="logo-image"
                            />
                            <h1 class="logo-text"><%= appName %></h1>
                        </div>
                        <button class="logout-button"> <div class = "option-content"> <h3 class="option-title">Logout</h3></div></button>
                    </div>
                </div>
            </header>

            <main class="main-content">
                <section class="hero-section">
                    <div class="hero-content">
                        <h1 class="hero-title">Convert Your Documents</h1>
                        <p class="hero-description">
                            Easily convert between DOC and PDF formats with just a few
                            clicks. Fast, secure, and reliable document conversion.
                        </p>
                    </div>
                </section>

                <section class="conversion-options">
                    <div class="options-container">
                        <div class="options-grid">
                            <div class="option-column">
                                <button class="conversion-option" onclick="handleDocToPdf()">
                                    <div class="option-content">
                                        <img
                                            src="https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/a99e9085f77b25b562f00f295b15f73a315b2341?placeholderIfAbsent=true"
                                            alt="DOC to PDF"
                                            class="option-icon"
                                        />
                                        <h3 class="option-title">DOC to PDF</h3>
                                        <p class="option-description">Convert Word documents to PDF format</p>
                                    </div>
                                </button>
                            </div>
                            <div class="option-column">
                                <button class="conversion-option" onclick="handlePdfToDoc()">
                                    <div class="option-content">
                                        <img
                                            src="https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/63a4d94a8d24335bd40ab2be51a0a9cd745250ab?placeholderIfAbsent=true"
                                            alt="PDF to DOC"
                                            class="option-icon"
                                        />
                                        <h3 class="option-title">PDF to DOC</h3>
                                        <p class="option-description">Convert PDF files to Word documents</p>
                                    </div>
                                </button>
                            </div>
                        </div>
                    </div>
                </section>

                <section class="recent-conversions">
                    <div class="conversions-content">
                        <header class="conversions-header">
                            <h2 class="conversions-title">Recent Conversions</h2>
                            <p class="conversions-subtitle">Last 30 days</p>
                        </header>

                        <div class="conversions-list">
                            <%
                                // JSP scriptlet for dynamic conversion history
                                String[][] conversions = {
                                    {"https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/cd747206a8c75182760ebe27ae4b3448a84762cd?placeholderIfAbsent=true", "Annual Report 2024.docx", "DOC → PDF • 2.3 MB", "2 hours ago", "https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/313b38d2b77dfe4c88afc592d47896a727f05cbc?placeholderIfAbsent=true"},
                                    {"https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/3d77e1f13a3bf00a4b4968691a9121b5750f7636?placeholderIfAbsent=true", "Contract Template.pdf", "PDF → DOC • 1.8 MB", "1 day ago", "https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/84facf8b4b7477cdce9d1adcd92967cfd2d7181d?placeholderIfAbsent=true"},
                                    {"https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/5e9078b08308f2d1f400b0b55d572a92eca4969f?placeholderIfAbsent=true", "Meeting Notes.docx", "DOC → PDF • 456 KB", "3 days ago", "https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/90be970a3f2d74d9d3ac0c8448cfe01ead2001b0?placeholderIfAbsent=true"},
                                    {"https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/22aaaf72d3e2f13f30d0e0dc7a7d5c3f88dbe102?placeholderIfAbsent=true", "Invoice_March_2024.pdf", "PDF → DOC • 892 KB", "1 week ago", "https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/e18a91b47406a827f51941c2c0969874955022e2?placeholderIfAbsent=true"},
                                    {"https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/1a63c90e2bd3eedaca212f667aa2ab53222cbb37?placeholderIfAbsent=true", "Project Proposal.docx", "DOC → PDF • 3.1 MB", "2 weeks ago", "https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/c3e69aa96f0dbf4bc709f3a2f884a6854ce96868?placeholderIfAbsent=true"}
                                };

                                for (int i = 0; i < conversions.length; i++) {
                                    String[] conversion = conversions[i];
                            %>
                            <div class="conversion-item">
                                <div class="item-info">
                                    <img
                                        src="<%= conversion[0] %>"
                                        alt="File icon"
                                        class="file-icon"
                                    />
                                    <div class="file-details">
                                        <h4 class="file-name"><%= conversion[1] %></h4>
                                        <p class="conversion-info"><%= conversion[2] %></p>
                                    </div>
                                </div>
                                <div class="item-actions">
                                    <time class="time-ago"><%= conversion[3] %></time>
                                    <img
                                        src="<%= conversion[4] %>"
                                        alt="Download"
                                        class="download-icon"
                                    />
                                </div>
                            </div>
                            <%
                                }
                            %>
                        </div>

                        <footer class="conversions-footer">
                            <button class="view-all-button" onclick="viewAllConversions()">
                                <span class="button-text">View All Conversions</span>
                                <img
                                    src="https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/34adeb1d3f5765661e4275b5f7ff2927ceeea41e?placeholderIfAbsent=true"
                                    alt="Arrow right"
                                    class="arrow-icon"
                                />
                            </button>
                        </footer>
                    </div>
                </section>
            </main>
        </div>
    </div>

    <!-- JSP Expression for dynamic content -->
    <div style="display: none;">
        <!-- Server info for debugging -->
        <p>Page generated on: <%= currentDate %></p>
        <p>Application: <%= appName %> v<%= appVersion %></p>
    </div>

    <script>
        function handleDocToPdf() {
            console.log('Converting DOC to PDF');
            // Could make AJAX call to JSP servlet for actual conversion
        }

        function handlePdfToDoc() {
            console.log('Converting PDF to DOC');
            // Could make AJAX call to JSP servlet for actual conversion
        }

        function viewAllConversions() {
            console.log('Viewing all conversions');
            // Could redirect to another JSP page
            window.location.href = 'allConversions.jsp';
        }

        // JSP can inject server-side data into JavaScript
        var serverTime = '<%= currentDate %>';
        console.log('Page loaded at server time:', serverTime);
    </script>
</body>
</html>
