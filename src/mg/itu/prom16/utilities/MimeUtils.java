package mg.itu.prom16.utilities;

import java.net.URLConnection;

public class MimeUtils {


    public static String getExtensionFromFileName(String filename) {
        String extension = null;
        int i = filename.lastIndexOf('.');
        if (i > 0 && i < filename.length() - 1) {
            extension = filename.substring(i + 1).toLowerCase();
        }
        return extension;
    }

    public static String getMimeType(String filename){
        String mimetype = URLConnection.guessContentTypeFromName(filename);
        if(mimetype != null){
            return mimetype;
        }
        String extension = getExtensionFromFileName(filename);

        switch (extension) {
            case "csv":
                mimetype = "text/csv";
                break;
            case "json":
                mimetype = "application/json";
                break;
            case "xml":
                mimetype = "application/xml";
                break;
            case "zip":
                mimetype = "application/zip";
                break;
            case "xlsx":
                mimetype = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                break;
            case "xls":
                mimetype = "application/vnd.ms-excel";
                break;
            case "docx":
                mimetype = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                break;
            case "pptx":
                mimetype = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
                break;
            case "mp3":
                mimetype = "audio/mpeg";
                break;
            case "mp4":
                mimetype = "video/mp4";
                break;
            default:
                break;
        }

        return mimetype;
       
    }
    
}
