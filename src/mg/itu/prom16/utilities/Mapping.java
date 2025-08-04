package mg.itu.prom16.utilities;

import java.lang.reflect.Method;
import java.util.HashMap;

public class Mapping {
    Class<?> className;
    Method method;
    String url;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getClassName() {
        return className;
    }

    public void setClassName(Class<?> className) {
        this.className = className;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    
    public Mapping(Class<?> classObj, Method method){
        this.setClassName(classObj);
        this.setMethod(method);
    }

    public Mapping(Class<?> className, Method method, String url) {
        this.className = className;
        this.method = method;
        this.url = url;
    }

    


   
    
}