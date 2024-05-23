package mg.itu.prom16.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mg.itu.prom16.utilities.Function;
import mg.itu.prom16.utilities.Mapping;

public class FrontController extends HttpServlet{
    List<Class<?>> liste;
    HashMap<String, Mapping> urlpattern;
    
    public void init() throws ServletException {
        super.init();
        findController();
    }
    public void findController(){
        try {
            liste = Function.ScanPackage(getInitParameter("controllerName"));  
            urlpattern = Function.getUrlController(liste);
              
        } catch (Exception e) {
            e.printStackTrace();
            liste = new ArrayList<>();
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
        String[] listeUrl=req.getRequestURI().split("/");
        if(listeUrl.length < 3){
            out.println("choisir le controller");
        }
        else{
            Mapping m = urlpattern.get(listeUrl[2]);
            if(m == null){
                out.println("Tsy misy ltyh ah");
            }
            else{
                m.show(out);
            }
        }
    }
}
