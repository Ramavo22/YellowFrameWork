package mg.itu.prom16.Controller;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "FrontController", urlPatterns = {"/*"})
public class FrontController extends HttpServlet{
    
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest req,HttpServletResponse resp) throws IOException{
        StringBuffer requestURL = req.getRequestURL();
        String requestedUrl = requestURL.toString();
        resp.setContentType("text/hrml");
        PrintWriter out = resp.getWriter();

        out.println(requestedUrl);
    }

}
