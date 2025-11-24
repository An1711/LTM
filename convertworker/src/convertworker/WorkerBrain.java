package convertworker;

import java.io.*;
import java.net.*;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.pdf.PdfDocument;

class WorkerBrain extends Thread {

    private final Socket socket;

    WorkerBrain(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        ) {
            // ===== 1. Nhận HEADER từ Server =====
            String header = dis.readUTF();
            System.out.println("Worker nhận HEADER: " + header);
            String[] parts = header.split("\\|");
            int type = Integer.parseInt(parts[1]);
            int userId = Integer.parseInt(parts[2]);
            String originalName = parts[3];
            long fileSize = Long.parseLong(parts[4]);

            // ===== 2. Lưu file tạm =====
            File tempFile = receiveTempFile(dis, originalName, fileSize);

            // ===== 3. Convert file =====
            convertAndSendBinary(type, originalName, tempFile, dos);

            // ===== 4. Gửi trả kết quả =====
            dos.flush();

            tempFile.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    //  Nhận file tạm từ server
    // ============================================================
    private File receiveTempFile(DataInputStream dis, String originalName, long fileSize) throws Exception {
        String tempName = "worker_temp_" + System.currentTimeMillis() + "_" + originalName;
        File temp = new File(tempName);

        try (FileOutputStream fos = new FileOutputStream(temp)) {
            byte[] buf = new byte[4096];
            long remain = fileSize;
            int r;

            while (remain > 0 && (r = dis.read(buf, 0, (int)Math.min(buf.length, remain))) != -1) {
                fos.write(buf, 0, r);
                remain -= r;
            }
        }
        return temp;
    }
    
    
    // ============================================================
    //  LOGIC CONVERT – phần bạn yêu cầu viết lại
    // ============================================================
    private void convertAndSendBinary(int type, String originalName, File inputFile,
            DataOutputStream dos) {
    	try {
    		String baseOutput = "worker_temp_converted_" + System.currentTimeMillis() + "_" + originalName;
    		File outputFile;

    		if (type == 1) {
    			// DOC → PDF
    			outputFile = new File(baseOutput + ".pdf");
    			Document doc = new Document();
    			doc.loadFromFile(inputFile.getAbsolutePath());
    			doc.saveToFile(outputFile.getAbsolutePath(), FileFormat.PDF);
    		} else {
    			// PDF → DOC
    			outputFile = new File(baseOutput + ".doc");
    			PdfDocument pdf = new PdfDocument();
    			pdf.loadFromFile(inputFile.getAbsolutePath());
    			pdf.saveToFile(outputFile.getAbsolutePath(), com.spire.pdf.FileFormat.DOC);
    		}

    		// ==== 2. Gửi file nhị phân qua socket ====
    		long fileSize = outputFile.length();
    		dos.writeUTF("FILE|OK|" + outputFile.getName() + "|" + fileSize);
    		dos.flush();

    		try (FileInputStream fis = new FileInputStream(outputFile)) {
    			byte[] buf = new byte[8192];
    			int r;
    			while ((r = fis.read(buf)) != -1) {
    				dos.write(buf, 0, r);
    			}
    		}
    		dos.flush();

    		// Xoá file tạm trên worker
    		outputFile.delete();

    	} catch (Exception e) {
    		try {
    			dos.writeUTF("FAIL|WORKER_CONVERT_ERROR:" + e.getMessage());
    			dos.flush();
    		} catch (IOException ioEx) {
    			ioEx.printStackTrace();
    		}
    		e.printStackTrace();
    	}
    }
   
}
