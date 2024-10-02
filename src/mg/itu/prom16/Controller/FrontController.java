package mg.itu.prom16.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.utilities.Function;
import mg.itu.prom16.utilities.Mapping;
import mg.itu.prom16.utilities.ModelView_Y;
import mg.itu.prom16.utilities.MySession;

public class FrontController extends HttpServlet{
    List<Class<?>> liste;
    HashMap<String, Mapping> urlpattern;
    ObjectMapper mapper;
    
    public void init() throws ServletException {
            super.init();
            findController();
            mapper = new ObjectMapper();
            
    }
    
    public void findController()throws ServletException{
        String packageName= getInitParameter("controllerName");
        // raha diso anarana lery amle web.xml
        if(packageName == null){
            throw new ServletException("verifier le Web.xml, parameter name doit etre controllerName");
        }
        else{
            try {
                liste = Function.ScanPackage(packageName);
                urlpattern = Function.getUrlController(liste);
            
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
        if(listeUrl.length < 3){
            out.println("choisir le controller");
            System.out.println("findController");
        }
        else{
            Mapping m = urlpattern.get(listeUrl[2]);
            if(m == null){
                throw new ServletException("Tsy misy ilay url "+listeUrl[2]);
            }
            else{

                /*
                 * Verb Test
                 */
                if(m.getVerb().compareToIgnoreCase(req.getMethod())!=0){
                    throw new ServletException("Tsy mifanaraka ilay verb any ilay methode sy ilay nampiasainao.Ny ao amin'ny methode: "+m.getVerb()+" nefa ny http methode namiasainao dia "+req.getMethod());
                }
                try {
                    MySession session = null;
                    if(Function.isMySessionArgument(m.getMethod())){
                        session = new MySession(req.getSession());
                    }
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
                            // req.getRequestDispatcher(modelView_Y.getUrl()).forward(req, resp);
                        }
                        // si Tsy String na ModelView_Y
                        if(!(val instanceof ModelView_Y) && !(val instanceof String)){
                            out.print("Na String na ModelView_Y ny class ampiasaina");
                        }
                    }
                } 
                catch (Exception e){
                    e.printStackTrace();
                }
                
            }
        }
    }
}
