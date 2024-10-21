package mg.itu.prom16.utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.http.Part;

public class MultipartFileHandler {
    String filename;
    byte[] content;

    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public byte[] getContent() {
        return content;
    }
    public void setContent(byte[] content) {
        this.content = content;
    }

    public MultipartFileHandler(){}

    public MultipartFileHandler(String filename, byte[] content){
        setFilename(filename);
        setContent(content);
    }

    public MultipartFileHandler(Part part)throws IOException{
        String filename = part.getSubmittedFileName();
        byte[] content = MultipartFileHandler.getBytesFromPart(part);

        setFilename(filename);
        setContent(content);
    }

    // Méthode pour convertir un Part en byte[]
    static byte[] getBytesFromPart(Part part) throws IOException{
        try (InputStream inputStream = part.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            
            int nRead;
            byte[] data = new byte[1024];
            
            // Lire les données du flux dans le buffer
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            
            buffer.flush();
            return buffer.toByteArray(); // Retourner le byte[]
        }
       
    }

}

