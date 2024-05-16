package mg.itu.prom16.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mg.itu.prom16.utilities.Function;

public class FrontController extends HttpServlet{
    Boolean checked = false;
    List<Class<?>> liste;
    public void init() throws ServletException {
        super.init();
        findController();
        
    }
    public void findController(){
        if(!checked){
            try {
                liste = Function.ScanPackage(getInitParameter("controllerName"));    
            } catch (Exception e) {
                e.printStackTrace();
                liste = new ArrayList<>();
            }
        }
    }
    
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest req,HttpServletResponse resp) throws IOException{
        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();
        if(liste.isEmpty()){
            out.println("Tsisy ltyh ah, tsisy");
        }
        else{
            out.println("Liste controllers");
            for (Class<?> class1 : liste) {
                out.println(class1.getSimpleName());
            }
        }
    }
}
