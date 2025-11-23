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
                        <div class="logout-container">
                        <form action="LogoutServlet" method="post">
    						<button class="logout-button">
        					<img src="https://img.icons8.com/ios-glyphs/30/ffffff/logout-rounded.png" alt="Logout Icon" class="logout-icon">
        					<span>Logout</span>
    						</button>
    						</form>
						</div>
                        
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
                            <form action="ConvertServlet" method= "post">
                            	<input type="hidden" name="type" value ="1" >	
                                <button type="submit" class="conversion-option">
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
                                </form>
                            </div>
                           
                            
                            <div class="option-column">
                            <form action="ConvertServlet" method= "post">
                            <input type="hidden" name="type" value ="0" >
                                <button type = "submit" class="conversion-option">
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
                                </form>
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

                        <%
    						java.util.Vector<model.BEAN.Link> links =
        						(java.util.Vector<model.BEAN.Link>) session.getAttribute("links");
						%>

				<div class="conversions-list">

					<%
    					if (links != null && !links.isEmpty()) {

        				for (model.BEAN.Link link : links) {

            			// icon khác nhau theo type
            			String icon = link.isType()
                    		? "https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/a99e9085f77b25b562f00f295b15f73a315b2341?placeholderIfAbsent=true"
                    		: "https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/63a4d94a8d24335bd40ab2be51a0a9cd745250ab?placeholderIfAbsent=true";

            			String typeText = link.getTypeText();
					%>

    				<div class="conversion-item">
        				<div class="item-info">
            				<img src="<%= icon %>" alt="File icon" class="file-icon" />

            				<div class="file-details">
                				<h4 class="file-name"><%= link.getLink() %></h4>
                				<p class="conversion-info"><%= typeText %></p>
            				</div>
        				</div>

        				<div class="item-actions">
            				<a href="<%= link.getLink() %>" download>
                			<img src="https://api.builder.io/api/v1/image/assets/734b695beb214eada95f691d10c9fe3f/34adeb1d3f5765661e4275b5f7ff2927ceeea41e?placeholderIfAbsent=true"
                     		alt="Download"
                     		class="download-icon"/>
            				</a>
        				</div>
    				</div>

			<%
        	}
    			} else {
			%>

    			<p style="color: gray; font-size: 14px; margin-left: 10px;">
       	 			No conversions found.
    			</p>

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
