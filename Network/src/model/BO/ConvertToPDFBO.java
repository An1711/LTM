package model.BO;

import java.io.File;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.pdf.PdfDocument;

import model.DAO.ConvertToPDFDAO;
import model.BEAN.Link;

public class ConvertToPDFBO {

    public String processConvert(int type, int userId, String originalFileName, String inputPath) {

        try {
            File downloads = new File("downloads");
            if (!downloads.exists()) downloads.mkdirs();

            String baseName = System.currentTimeMillis() + "_" + originalFileName;
            String outPath;

            // ========================
            // TYPE 1: DOC → PDF
            // ========================
            if (type == 1) {
                if (!originalFileName.toLowerCase().endsWith(".pdf")) {
                    outPath = new File(downloads, baseName + ".pdf").getAbsolutePath();
                } else {
                    outPath = new File(downloads, baseName).getAbsolutePath();
                }

                try {
                    Document doc = new Document();
                    doc.loadFromFile(inputPath);
                    doc.saveToFile(outPath, FileFormat.PDF);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return "FAIL|CONVERT_ERROR:" + ex.getMessage();
                }

            } else {
                // ========================
                // TYPE 0: PDF → DOC
                // ========================
                if (!originalFileName.toLowerCase().endsWith(".doc") &&
                    !originalFileName.toLowerCase().endsWith(".docx")) {
                    outPath = new File(downloads, baseName + ".doc").getAbsolutePath();
                } else {
                    outPath = new File(downloads, baseName).getAbsolutePath();
                }

                try {
                    PdfDocument pdf = new PdfDocument();
                    pdf.loadFromFile(inputPath);
                    pdf.saveToFile(outPath, com.spire.pdf.FileFormat.DOC);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return "FAIL|CONVERT_ERROR:" + ex.getMessage();
                }
            }

            // ========================
            // Gọi DAO để lưu kết quả
            // ========================
            ConvertToPDFDAO dao = new ConvertToPDFDAO();
            Link l = new Link();
            l.setID(userId);
            l.setType(type == 1);
            l.setLink(outPath);

            if (!dao.saveLink(l)) {
                return "FAIL|DB_SAVE_FAILED";
            }

            return "OK|" + outPath;

        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL|EXCEPTION:" + e.getMessage();
        }
    }
    
    public boolean saveConvertedLink(int type, int userId, String outPath) {
        try {
            ConvertToPDFDAO dao = new ConvertToPDFDAO();
            Link l = new Link();
            l.setID(userId);
            l.setType(type == 1);
            l.setLink(outPath);

            return dao.saveLink(l);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
