package mg.itu.prom16.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
import mg.itu.prom16.utilities.MimeUtils;
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
            initMyExeptions.add(new MyExeption("Vérifier le Web.xml, parameter name doit etre controllerName",500));  
        }
        else{
            try {
                liste = Function.ScanPackage(packageName);
                urlpattern = Function.getUrlController(liste);
            }
            catch(MyExeption myE){
                initMyExeptions.add(myE);
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
        resp.setContentType("text/html");
        
        
        if(!initMyExeptions.isEmpty()){
            PrintWriter out = resp.getWriter();
            for (MyExeption myExeption : initMyExeptions) {
                resp.setStatus(myExeption.getStatusCode()); // Définit le statut HTTP
                String error = Function.generateErrorPage(myExeption,myExeption.getStatusCode());
                out.println(error);
            }
            out.close(); // Ferme le flux de sortie
            return;
        }
       
        // split pour avoir les urlpatters
        String[] listeUrl=req.getRequestURI().split("/");
        String url = listeUrl.length > 2 ? String.join("/", Arrays.copyOfRange(listeUrl, 2, listeUrl.length)) : "";

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
                if (part.getSubmittedFileName() != null) {
                    try {
                        String paraName = part.getName();
                        MultipartFileHandler fileHandler = new MultipartFileHandler(part);
                        String jsonFile =  mapper.writeValueAsString(fileHandler);
                        parameterValue.put(paraName,jsonFile);
                    } catch (IOException e) {
                        PrintWriter out = resp.getWriter();
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.println(Function.generateErrorPage(e, HttpServletResponse.SC_BAD_REQUEST));
                        out.close(); // Ferme le flux de sortie
                    }
                }
            }
        }

       
        if(listeUrl.length < 3){
            PrintWriter out = resp.getWriter();
            out.println("choisir le controller");
            System.out.println("findController");
            out.close(); // Ferme le flux de sortie
        }
        else{
            String key = req.getAttribute("METHOD") != null ? url+"/"+req.getAttribute("METHOD").toString() : url+"/"+req.getMethod();
            Mapping m = urlpattern.get(key);
            if(m == null){
                PrintWriter out = resp.getWriter();
                MyExeption ex = new MyExeption("Tsy misy ilay url/methode "+key, 404);
                resp.setStatus(ex.getStatusCode()); // Définit le statut HTTP
                out.println(Function.generateErrorPage(ex, ex.getStatusCode()));
                out.close(); // Ferme le flux de sortie
            }
            else{
                try { 
                    MySession session = new MySession(req.getSession());
                   
                    Object val = Function.executeMethode(m,parameterValue,session,req,resp);
                    /* 
                     * Verify if the method is annoted by @RestAPI
                     */
                    Method method = m.getMethod();
                    /*
                     * If the method is Annoted by RestAPI
                     */
                    if(Function.isMethodAnnotedByRestAPI(method)){
                        resp.setContentType("application/json"); // Définit le type de contenu de la réponse
                        resp.setCharacterEncoding("UTF-8"); // Définit l'encodage de la réponse
                        PrintWriter out = resp.getWriter();
                        if(val instanceof ModelView_Y){
                            resp.setContentType("application/json"); // Définit le type de contenu de la réponse
                            ModelView_Y modelView_Y = (ModelView_Y) val;
                            String json = mapper.writeValueAsString(modelView_Y.getData());
                            out.println(json);
                        }
                        else{
                            out.println(mapper.writeValueAsString(val));
                        }
                        out.close(); // Ferme le flux de sortie
                    }
                    /*
                     * If the method is not annoted by restAPI
                    */

                    if(!Function.isMethodAnnotedByRestAPI(m.getMethod())){
                        if(val instanceof String){
                            PrintWriter out = resp.getWriter();
                            out.println(val); // Utilisez out.println pour envoyer la réponse au client
                            out.close(); // Ferme le flux de sortie
                        }
                        if(val instanceof ModelView_Y){
                            ModelView_Y modelView_Y = (ModelView_Y) val;
                            for(Entry<String, Object> data : modelView_Y.getData().entrySet()){
                                req.setAttribute(data.getKey(), data.getValue());
                            }
                            req.getRequestDispatcher("/"+modelView_Y.getUrl()).forward(req, resp);
                        }
                        if(val instanceof MultipartFileHandler){
                            MultipartFileHandler file = (MultipartFileHandler) val;
                            String fileName = file.getFilename();
                            String contentTypeForResponse = MimeUtils.getMimeType(fileName);
                            resp.setContentType(contentTypeForResponse); // Définit le type de contenu de la réponse
                            resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                            byte[] fileContent = file.getContent(); 
                            resp.getOutputStream().write(fileContent); // Écrit le contenu du fichier dans la réponse
                            resp.getOutputStream().flush(); // Assurez-vous de vider le flux après l'écriture
                            resp.getOutputStream().close(); // Ferme le flux de sortie

                        }
                        // si Tsy String na ModelView_Y
                        if(!(val instanceof ModelView_Y) && !(val instanceof String)){
                            PrintWriter out = resp.getWriter();
                            out.print("Na String na ModelView_Y ny class ampiasaina");
                            out.close(); // Ferme le flux de sortie
                        }


                    }
                } 
                catch (MyExeption ex) {
                    PrintWriter out = resp.getWriter();
                    // Erreur personnalisée
                    resp.setStatus(ex.getStatusCode()); // Définit le statut HTTP
                    out.println(Function.generateErrorPage(ex, ex.getStatusCode()));
                } catch (Exception e) {
                    PrintWriter out = resp.getWriter();
                    // Capture les autres exceptions
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Définit le statut 500
                    out.println(Function.generateErrorPage(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
                    out.close(); // Ferme le flux de sortie
                    e.printStackTrace(); // Log dans la console pour le débogage
                }
            }
        }
    }
}
