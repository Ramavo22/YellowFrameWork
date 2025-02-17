package mg.itu.prom16.utilities;

import java.lang.reflect.Method;
import java.util.HashMap;

public class Mapping {
    Class<?> className;
    HashMap<String,Method> methods;
    Method method;

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

    public HashMap<String, Method> getMethods() {
        return methods;
    }

    public void setMethods(HashMap<String, Method> methods) {
        this.methods = methods;
    }

    public Mapping(Class<?> className){
        this.setClassName(className);
        this.methods = new HashMap<>();
    }


    public Mapping(Class<?> classObj, Method method){
        this.setClassName(classObj);
        this.setMethod(method);
    }


   
    
}