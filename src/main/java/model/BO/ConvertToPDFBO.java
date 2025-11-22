package model.BO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import model.BEAN.Link;
import model.DAO.ConvertToPDFDAO;

public class ConvertToPDFBO {
    
    private ConvertToPDFDAO convertDAO;
    
    public ConvertToPDFBO() {
        convertDAO = new ConvertToPDFDAO();
    }
    
    /**
     * SỬA LẠI: Nhận thêm tham số outputDir từ Controller
     * Chuyển đổi file DOC sang PDF và lưu link vào database
     * @param docFilePath đường dẫn file .doc nguồn (file tạm)
     * @param originalFileName tên file gốc
     * @param outputDir đường dẫn thư mục để lưu file PDF output
     * @return đường link download file PDF
     */
    public String convertDocToPdfAndSave(String docFilePath, String originalFileName, String outputDir, int userID) {
        String pdfFileName = UUID.randomUUID().toString() + "_" + 
            originalFileName.replaceAll("(?i)\\.docx?$", ".pdf"); // Regex để thay thế .doc hoặc .docx
        String pdfFilePath = outputDir + File.separator + pdfFileName;
        try {
            // 2. Chuyển đổi DOC sang PDF
            boolean conversionSuccess = convertDocToPdf(docFilePath, pdfFilePath, originalFileName);
            
            if (!conversionSuccess) {
                System.err.println("Không thể chuyển đổi file!");
                return null;
            }
            
            // 3. Tạo link download
            String downloadLink = "downloads/" + pdfFileName;
            // 4. Tạo đối tượng Link (type = true cho DOC→PDF)
            Link link = new Link(userID, downloadLink, true);
            
            // 5. Gọi DAO để lưu link vào database
            boolean saved = convertDAO.saveLink(link);
            
            if (!saved) {
                System.err.println("Không thể lưu link vào database!");
                new File(pdfFilePath).delete();
                return null;
            }
            
            System.out.println("Chuyển đổi DOC→PDF và lưu link thành công: " + downloadLink);
            return downloadLink;
            
        } catch (Exception e) {
            e.printStackTrace();
            // Xóa file PDF nếu có lỗi
            File pdfFile = new File(pdfFilePath);
            if (pdfFile.exists()) {
                pdfFile.delete();
            }
            return null;
        }
    }
    
    /**
     * SỬA LẠI: Tương tự, nhận outputDir từ Controller
     */
    public String convertPdfToDocAndSave(String pdfFilePath, String originalFileName, String outputDir, int userID) {
        String docFileName = UUID.randomUUID().toString() + "_" + 
            originalFileName.replaceAll("(?i)\\.pdf$", ".docx");
        String docFilePath = outputDir + File.separator + docFileName;
        
        try {
            boolean conversionSuccess = convertPdfToDoc(pdfFilePath, docFilePath);
            
            if (!conversionSuccess) {
                System.err.println("Không thể chuyển đổi file!");
                return null;
            }
            
            String downloadLink = "downloads/" + docFileName;
            Link link = new Link(userID, downloadLink, false);
            boolean saved = convertDAO.saveLink(link);
            
            if (!saved) {
                System.err.println("Không thể lưu link vào database!");
                new File(docFilePath).delete();
                return null;
            }
            
            System.out.println("Chuyển đổi PDF→DOC và lưu link thành công: " + downloadLink);
            return downloadLink;
            
        } catch (Exception e) {
            e.printStackTrace();
            File docFile = new File(docFilePath);
            if (docFile.exists()) {
                docFile.delete();
            }
            return null;
        }
    }
    
    /**
     * SỬA LẠI:
     * 1. Nhận vào đường dẫn file output thay vì tự tạo
     * 2. Dùng try-with-resources để đảm bảo stream được đóng, tránh lỗi file bị khóa
     * 3. Trả về boolean (thành công/thất bại)
     */
    private boolean convertDocToPdf(String docFilePath, String pdfFilePath, String originalFileName) {
        try {
            // Khởi tạo Document Spire.Doc từ file DOC/DOCX
            com.spire.doc.Document document = new com.spire.doc.Document();
            document.loadFromFile(docFilePath);

            // Chuyển đổi sang PDF
            document.saveToFile(pdfFilePath, com.spire.doc.FileFormat.PDF);

            // Đóng tài nguyên
            document.close();

            System.out.println("Chuyển đổi DOC→PDF bằng Spire.Doc thành công: " + pdfFilePath);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi chuyển đổi DOC→PDF bằng Spire.Doc: " + e.getMessage());
            return false;
        }
    }

    
    /**
     * SỬA LẠI: Tương tự, dùng try-with-resources
     */
    private boolean convertPdfToDoc(String pdfFilePath, String docFilePath) {
        try {
            // Load file PDF
            com.spire.pdf.PdfDocument pdf = new com.spire.pdf.PdfDocument();
            pdf.loadFromFile(pdfFilePath);

            // Lưu sang DOCX
            pdf.saveToFile(docFilePath, com.spire.pdf.FileFormat.DOCX);

            pdf.close();

            System.out.println("Chuyển đổi PDF→DOC thành công: " + docFilePath);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi chuyển đổi PDF→DOC bằng Spire.PDF: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy thông tin link theo ID
     */
    public Link getLinkById(int id) {
        return convertDAO.getLinkById(id);
    }
    
    /**
     * Lấy tất cả link đã chuyển đổi
     */
    public Vector<Link> getDataList(int userID) {
        return new Vector<>(convertDAO.getListLinkById(userID));
    }
    
    /**
     * Lấy tất cả link đã chuyển đổi
     */
    public List<Link> getAllLinks() {
        return convertDAO.getAllLinks();
    }
    
    /**
     * Lấy danh sách link theo loại
     * @param type true: DOC→PDF, false: PDF→DOC
     */
    public List<Link> getLinksByType(boolean type) {
        return convertDAO.getLinksByType(type);
    }
    
    /**
     * SỬA LẠI: Nhận đường dẫn gốc của ứng dụng để xóa file chính xác
     * @param linkId ID của link cần xóa
     * @param appPath Đường dẫn thực của ứng dụng web (từ getServletContext().getRealPath(""))
     */
    public boolean deleteLink(int linkId, String appPath) {
        Link link = convertDAO.getLinkById(linkId);
        if (link != null) {
            // Xóa file vật lý
            // link.getLink() có dạng "downloads/ten_file.pdf"
            // Ta cần ghép nó với đường dẫn gốc của app để có đường dẫn tuyệt đối
            String fullPath = appPath + File.separator + link.getLink().replace("/", File.separator);
            
            File file = new File(fullPath);
            if (file.exists()) {
                file.delete();
            }
            
            // Xóa link trong database
            return convertDAO.deleteLink(linkId);
        }
        return false;
    }
    
    /**
     * Đếm số lượng theo loại
     */
    public int getCountByType(boolean type) {
        return convertDAO.getCountByType(type);
    }
    
    /**
     * Kiểm tra file có tồn tại không
     */
    public boolean isFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }
}