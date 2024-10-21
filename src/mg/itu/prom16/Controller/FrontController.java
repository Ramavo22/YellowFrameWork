package mg.itu.prom16.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import mg.itu.prom16.execption.MyExeption;
import mg.itu.prom16.utilities.Function;
import mg.itu.prom16.utilities.Mapping;
import mg.itu.prom16.utilities.ModelView_Y;
import mg.itu.prom16.utilities.MultipartFileHandler;
import mg.itu.prom16.utilities.MySession;


@MultipartConfig
public class FrontController extends HttpServlet{
    List<Class<?>> liste;
    HashMap<String, Mapping> urlpattern;
    ObjectMapper mapper;
    List<MyExeption> initMyExeptions;
    
    public void init() throws ServletException {
            super.init();
            initMyExeptions = new ArrayList<>();
            findController();
            mapper = new ObjectMapper();
            
    }
    
    public void findController()throws ServletException{
        String packageName= getInitParameter("controllerName");
        // raha diso anarana lery amle web.xml
        if(packageName == null){
            initMyExeptions.add(new MyExeption("verifier le Web.xml, parameter name doit etre controllerName",500));  
        }
        else{
            try {
                liste = Function.ScanPackage(packageName);
                urlpattern = Function.getUrlController(liste);
            }
            catch(MyExeption myE){
                initMyExeptions.add(myE);
            }
            catch(ServletException es){
                throw es;
            }
             catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }


    protected void processRequest(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException{
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        if(!initMyExeptions.isEmpty()){
            for (MyExeption myExeption : initMyExeptions) {
                resp.setStatus(myExeption.getStatusCode()); // Définit le statut HTTP
                out.println("{\"error\": \"" + myExeption.getMessage() + "\", \"status\": " + myExeption.getStatusCode() + "}");
            }
            return;
        }
       
        // split pour avoir les urlpatters
        String[] listeUrl=req.getRequestURI().split("/");

        // recuperer les clés des paramètres
        Enumeration<String> parameterNames = req.getParameterNames();

        // tableau associatif pour stocker les valeurs des données envoyé
        HashMap<String,String> parameterValue = new HashMap<>();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            parameterValue.put(paramName, req.getParameter(paramName));
        }

        
        String contentType = req.getContentType();
        if (contentType != null && contentType.startsWith("multipart/form-data")) {
            Collection<Part> parts = req.getParts();

            for (Part part : parts) {
                try {
                    String paraName = part.getName();
                    System.out.println(paraName+" "+part.getSubmittedFileName()+" "+part.getSize());
                    MultipartFileHandler fileHandler = new MultipartFileHandler(part);
                    System.out.println(mapper.writeValueAsString(fileHandler));
                    parameterValue.put(paraName, mapper.writeValueAsString(fileHandler));
                } catch (IOException e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("{\"error\": \""+e.getMessage()+"\"}");
                }
            }
        }

       
        if(listeUrl.length < 3){
            out.println("choisir le controller");
            System.out.println("findController");
        }
        else{
            String key = listeUrl[2]+"/"+req.getMethod();
            Mapping m = urlpattern.get(key);
            if(m == null){
                MyExeption ex = new MyExeption("Tsy misy ilay url/methode "+key, 404);
                resp.setStatus(ex.getStatusCode()); // Définit le statut HTTP
                out.println("{\"error\": \"" + ex.getMessage() + "\", \"status\": " + ex.getStatusCode() + "}");

            }
            else{
                try { 
                    MySession session = null;
                    if(Function.isMySessionArgument(m.getMethod())){
                        session = new MySession(req.getSession());
                    }
                    /*
                     * Savoir si il y a des fichier envoyer
                     */
                    
                    
                    Object val = Function.executeMethode(m,parameterValue,session);
                    /* 
                     * Verify if the method is annoted by @RestAPI
                     */
                    Method method = m.getMethod();
                    System.err.println(Function.isMethodAnnotedByRestAPI(method));
                    /*
                     * If the method is Annoted by RestAPI
                     */
                    if(Function.isMethodAnnotedByRestAPI(method)){
                        if(val instanceof ModelView_Y){
                            ModelView_Y modelView_Y = (ModelView_Y) val;
                            String json = mapper.writeValueAsString(modelView_Y.getData());
                            out.println(json);
                        }
                        else{
                            out.println(mapper.writeValueAsString(val));
                        }
                    }
                    /*
                     * If the method is not annoted by restAPI
                     */

                    if(!Function.isMethodAnnotedByRestAPI(m.getMethod())){
                        if(val instanceof String){
                            out.println(val); // Utilisez out.println pour envoyer la réponse au client
                        }
                        if(val instanceof ModelView_Y){
                            ModelView_Y modelView_Y = (ModelView_Y) val;
                            for(Entry<String, Object> data : modelView_Y.getData().entrySet()){
                                req.setAttribute(data.getKey(), data.getValue());
                            }
                            System.out.println(modelView_Y.getUrl());
                            req.getRequestDispatcher("/"+modelView_Y.getUrl()).forward(req, resp);
                        }
                        // si Tsy String na ModelView_Y
                        if(!(val instanceof ModelView_Y) && !(val instanceof String)){
                            out.print("Na String na ModelView_Y ny class ampiasaina");
                        }
                    }
                } 
                catch (MyExeption ex) {
                    // Erreur personnalisée
                    resp.setStatus(ex.getStatusCode()); // Définit le statut HTTP
                    out.println("{\"error\": \"" + ex.getMessage() + "\", \"status\": " + ex.getStatusCode() + "}");
                } catch (Exception e) {
                    // Capture les autres exceptions
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Définit le statut 500
                    out.println("{\"error\": \"Erreur interne du serveur\", \"message\": \"" + e.getMessage() + "\"}");
                    e.printStackTrace(); // Log dans la console pour le débogage
                } finally {
                    out.close(); // Ferme le PrintWriter
                }
            }
        }
    }
}
