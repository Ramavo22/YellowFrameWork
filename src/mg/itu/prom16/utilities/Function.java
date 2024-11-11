package mg.itu.prom16.utilities;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import mg.itu.prom16.annotation.Controller_Y;
import mg.itu.prom16.annotation.Get;
import mg.itu.prom16.annotation.Url;
import mg.itu.prom16.execption.MyExeption;
import mg.itu.prom16.annotation.Param;
import mg.itu.prom16.annotation.Post;
import mg.itu.prom16.annotation.RestAPI;


public class Function {
    // control des annotations
    public static Boolean isAnnoted(Object obj,Class<? extends Annotation> annotation){
        return obj.getClass().isAnnotationPresent(annotation);
    }
    
    public static Boolean isController_Y(Object obj){
        return Function.isAnnoted(obj, Controller_Y.class);
    }

    public static boolean isMethodAnnotedByRestAPI(Method method) {
        return method.isAnnotationPresent(RestAPI.class);
    }

    public static boolean isMethodAnnotedByUrl(Method method){
        return method.isAnnotationPresent(Url.class);
    }
    

    public static Path getPathProject(String dossier){
        String repertoireTravail = System.getProperty("user.dir");
        Path pathRepertoireTravail = Paths.get(repertoireTravail);
        Path cheminDossierRessource = pathRepertoireTravail.resolve(dossier);
        return cheminDossierRessource;
    }

    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    // Liste de controller dans un packages
    public static List<Class<?>> ScanPackage(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        
       

        Enumeration<URL> resources = classLoader.getResources(path);

        if (!resources.hasMoreElements()) {
            throw new MyExeption("Package " + packageName + " does not exist.", 500);
        }

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                File directory = new File(URLDecoder.decode(resource.getFile(), "UTF-8"));
                if (directory.exists() && directory.isDirectory()) {
                    File[] files = directory.listFiles();
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".class")) {
                            String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                            Class<?> clazz = Class.forName(className);
                            if (clazz.isAnnotationPresent(Controller_Y.class)) {
                                classes.add(clazz);
                            }
                        }
                    }
                }
            }
        }
        // Raha tsy misy controller ilay package
        if(classes.isEmpty()){
            throw new MyExeption("Tsy misy controller mihitsy ny package nao. Ity ilay package "+packageName, 500);
        }
        return classes;
        
    }

    public static HashMap<String,Mapping> getUrlController(List<Class<?>> listeController)throws Exception{
        HashMap<String,Mapping> valiny = new HashMap<>();
        /*
         * Check every controller if his action method is annoted by @Url
         */
        for (Class<?> controller : listeController) {
            System.out.println(controller.getName());
            Method[] listeMethod = controller.getMethods();
            for (Method method : listeMethod) {
                System.out.println("\t"+method.getName()+": "+method.isAnnotationPresent(Url.class));
                if(method.isAnnotationPresent(Url.class)){
                    /*
                     * Initialisation of the potential new key
                     */
                    String key = "";
                    Url annotationMethode = method.getAnnotation(Url.class);
                    String url = annotationMethode.url();
                    /*
                     * Check if the method is define correctly
                     */
                    boolean isGet = method.isAnnotationPresent(Get.class);
                    boolean isPost = method.isAnnotationPresent(Post.class);
                    String newVerb = "";
                    if(isGet && isPost){
                        throw new MyExeption("Misy olana, controlleur sady get no post", 500);
                    }
                    if(isPost){
                       newVerb = "POST";
                    }
                    if (isGet || (!isPost && !isGet)) {
                        newVerb = "GET";
                    }
                    key = url+"/"+newVerb;
                    System.out.println("\t\t"+key);
                    /*
                     * if the key is already used
                     */
                    if(valiny.containsKey(key)){
                        throw new MyExeption("le url avec cette methode est déja utilisé ", 500);    
                    }
                    else{
                        /*
                         * new key, add value Mapping for the key
                         */
                        Mapping map = new Mapping(controller, method);
                        valiny.put(key, map);
                    }
                    
                }
            }
        }
        return valiny;
    }




    private static Object convertType(String param, Class<?> targetType,HashMap<String,String> parameters) throws Exception {
        if (targetType == String.class) {
            return parameters.get(param);
        } else if (targetType == int.class || targetType == Integer.class) {
            return (int) Integer.parseInt(parameters.get(param));
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return (boolean) Boolean.parseBoolean(parameters.get(param));
        } else if (targetType == long.class || targetType == Long.class) {
            return (long) Long.parseLong(parameters.get(param));
        } else if (targetType == double.class || targetType == Double.class) {
            return (double) Double.parseDouble(parameters.get(param));
        } else if (targetType == float.class || targetType == Float.class) {
            return (float) Float.parseFloat(parameters.get(param));
        } else if(targetType == MultipartFileHandler.class){
            ObjectMapper mapper = new ObjectMapper();
            System.out.println(parameters.get(param));
            MultipartFileHandler fileHandler = mapper.readValue(parameters.get(param),MultipartFileHandler.class);
            return fileHandler;
        }
        else {
            return convertCustomType(param, targetType ,parameters);
        }
    }

    private static Object convertCustomType(String param, Class<?> targetType,HashMap<String,String> parameters) throws Exception {
        Object instance = targetType.getDeclaredConstructor().newInstance();
        Field[] fields = targetType.getDeclaredFields();   
        for (Field field : fields) {
            String fieldName = field.getName();
            String allparam = param+"."+fieldName;
            Object paramValue = Function.convertType(allparam,field.getType(),parameters);
            Method m = targetType.getMethod("set"+Function.capitalizeFirstLetter(fieldName),paramValue.getClass());
            m.invoke(instance, paramValue);
        }
        return instance;
    }

    public static Object executeMethode(Mapping map,Object... args) throws Exception{
        return map.getMethod().invoke(map.getClassName().getConstructor().newInstance(),args);
    }

    public static Object executeMethode(Mapping map,HashMap<String,String> parameters,MySession session)throws Exception{
        Method method = map.getMethod();
        Parameter[] methodParameter = method.getParameters();
        Object[] args = new Object[methodParameter.length];
        String parameterName = new String();
        int i = 0;
        for (i = 0; i < methodParameter.length; i++) {
            if(methodParameter[i].isAnnotationPresent(Param.class)){
                Param param = methodParameter[i].getAnnotation(Param.class);
                parameterName = param.name();
            }
            else{
                parameterName = methodParameter[i].getName();
            }
            if(methodParameter[i].getType() == MySession.class){
                args[i] = session;
            }
            else{
                args[i] = Function.convertType(parameterName, methodParameter[i].getType(), parameters);
            }
            
        }
        return Function.executeMethode(map, args);
    }

    // fonction de verification de la classe MySession

    public static boolean isMySessionArgument(Method m){
        Class<?>[] argClass = m.getParameterTypes();
        for (Class<?> class1 : argClass) {
            if(class1 == MySession.class) {
                return true;
            }
        }
        return false;
    }

    static String traductionStatusCode(int statut){
        String val = "";
        switch (statut) {
            case 404:
                val = "NOT FOUND";
                break;
            
            case 500: 
                val = "INTERNAL ERROR";
                break;

            /*
            *   tohizana
            */
        }
        return val;
    }


     // Fonction qui prend une exception en argument et génère une page HTML
    public static String generateErrorPage(Exception ex, int statusCode) {
        // Convertir l'exception et son stack trace en chaîne de caractères
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);  // Imprimer la trace dans le StringWriter
        String stackTrace = sw.toString();

        // Construire le HTML avec le stack trace
        String htmlPage = "<!DOCTYPE html>\r\n" + //
                        "<html lang=\"fr\">\r\n" + //
                        "<head>\r\n" + //
                        "    <meta charset=\"UTF-8\">\r\n" + //
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" + //
                        "    <title>Erreur</title>\r\n" + //
                        "    <style>\r\n" + //
                        "        body {\r\n" + //
                        "            margin: 0;\r\n" + //
                        "            padding: 0;\r\n" + //
                        "            height: 100vh;\r\n" + //
                        "            display: flex;\r\n" + //
                        "            justify-content: center;\r\n" + //
                        "            align-items: center;\r\n" + //
                        "            font-family: Arial, sans-serif;\r\n" + //
                        "            background-color: #000000;\r\n" + //
                        "        }\r\n" + //
                        "        .error-container {\r\n" + //
                        "            background-color: rgb(46, 46, 46);\r\n" + //
                        "            padding: 20px;\r\n" + //
                        "            border-radius: 8px;\r\n" + //
                        "            box-shadow: 0 4px 8px rgba(83, 83, 83, 0.1);\r\n" + //
                        "            text-align: center;\r\n" + //
                        "            color: #353535;\r\n" + //
                        "        }\r\n" + //
                        "        .error-container h1 {\r\n" + //
                        "            color: #ff0000;\r\n" + //
                        "            font-size: 48px;\r\n" + //
                        "        }\r\n" + //
                        "        .error-container pre {\r\n" + //
                        "            color: #e4dfdf;\r\n" + //
                        "            text-align: left;\r\n" + //
                        "            background-color: #333;\r\n" + //
                        "            padding: 10px;\r\n" + //
                        "            border-radius: 4px;\r\n" + //
                        "            overflow: auto;\r\n" + //
                        "        }\r\n" + //
                        "        .error-container p{\r\n" + //
                        "            color: antiquewhite;\r\n" + //
                        "        }\r\n" + //
                        "    </style>\r\n" + //
                        "</head>\r\n" + //
                        "<body>\r\n" + //
                        "    <div class=\"error-container\">\r\n" + //
                        "        <h1>Erreur "+statusCode+":"+Function.traductionStatusCode(statusCode)+"</h1>\r\n" + //
                        "        <p> <span style=\"color: #ff0000;\">Une errreur a été levée durant l'éxécution</span></p>\r\n" + //
                        "        <p><strong><span style=\"color: #ff0000;\">Type de l'exception:</span></strong> "+ex.getClass()+" </p>\r\n" + //
                        "        <pre>"+stackTrace+"</pre> <!-- Affichage de l'exception -->\r\n" + //
                        "    </div>\r\n" + //
                        "</body>\r\n" + //
                        "</html>\r\n";
        // Retourner la page HTML complète
        return htmlPage;
    }
}

