package mg.itu.prom16.utilities;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import mg.itu.prom16.annotation.Controller_Y;
import mg.itu.prom16.annotation.Get_Y;

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

    // Liste de controller dans un packages

    public static List<Class<?>> ScanPackage(String packageName) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        
        Enumeration<URL> resources = classLoader.getResources(path);
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
        return classes;
        
    }

    public static HashMap<String,Mapping> getUrlController(List<Class<?>> listeController){
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
                    Mapping map = new Mapping(controller, method);
                    valiny.put(url, map);
                }
            }
        }
        return valiny;
    }

        public static Object executeMethode(Mapping map,Object... args) {
        try {
            return map.getMethod().invoke(map.getClassName().newInstance(),args);
        } catch ( Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return e.toString();
        }
    }


   



    
}
