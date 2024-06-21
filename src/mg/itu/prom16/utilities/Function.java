package mg.itu.prom16.utilities;

import java.io.File;
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

import jakarta.servlet.ServletException;
import mg.itu.prom16.annotation.Controller_Y;
import mg.itu.prom16.annotation.Get_Y;
import mg.itu.prom16.annotation.Param;


public class Function {
    // control des annotations
    public static Boolean isAnnoted(Object obj,Class<? extends Annotation> annotation){
        return obj.getClass().isAnnotationPresent(annotation);
    }
    
    public static Boolean isController_Y(Object obj){
        return Function.isAnnoted(obj, Controller_Y.class);
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

    public static List<Class<?>> ScanPackage(String packageName) throws Exception,ServletException {
        List<Class<?>> classes = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        
        Enumeration<URL> resources = classLoader.getResources(path);

        if (!resources.hasMoreElements()) {
            throw new ServletException("Package " + packageName + " does not exist.");
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
            throw new ServletException("Tsy misy controller mihitsy ny package nao. Ity ilay package "+packageName);
        }
        return classes;
        
    }

    public static HashMap<String,Mapping> getUrlController(List<Class<?>> listeController)throws Exception{
        HashMap<String,Mapping> valiny = new HashMap<>();
        // parcourir les controlllers
        for (Class<?> controller : listeController) {
            // avoir la liste methode dans le controllers
            Method[] listeMethod = controller.getMethods();
            // parcourir chaque methode dans le controllers
            for (Method method : listeMethod) {
                // si annotée
                if(method.isAnnotationPresent(Get_Y.class)){
                    Get_Y annotationMethode = method.getAnnotation(Get_Y.class);
                    String url = annotationMethode.url();
                    if(valiny.containsKey(url)){
                        throw new ServletException("Efa niverina ilay url "+url+" amin'ny ity methode "+method.getName());
                    }
                    Mapping map = new Mapping(controller, method);
                    valiny.put(url, map);
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
        } else {
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
        return map.getMethod().invoke(map.getClassName().newInstance(),args);
    }

    public static Object executeMethode(Mapping map,HashMap<String,String> parameters)throws Exception{
        Method method = map.getMethod();
        Parameter[] methodParameter = method.getParameters();
        Object[] args = new Object[methodParameter.length];
        String parameterName = new String();

        for (int i = 0; i < methodParameter.length; i++) {
            if(methodParameter[i].isAnnotationPresent(Param.class)){
                Param param = methodParameter[i].getAnnotation(Param.class);
                parameterName = param.name();
            }
            else {
                parameterName = methodParameter[i].getName();
            }
            // String value = parameters.get(parameterName);
            System.out.println(parameterName);
            // args[i] =  value;
            args[i] = Function.convertType(parameterName, methodParameter[i].getType(), parameters);
            System.out.println(args[i].toString());
        }

        return Function.executeMethode(map, args);
    }
    
}
