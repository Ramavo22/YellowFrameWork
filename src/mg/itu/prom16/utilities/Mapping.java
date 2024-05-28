package mg.itu.prom16.utilities;

import java.io.PrintWriter;
import java.lang.reflect.Method;

public class Mapping {
    Class<?> className;
    Method method;

    public Class<?> getClassName() {
        return className;
    }

    public void setClassName(Class<?> className) {
        this.className = className;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    

    public Mapping(Class<?> className, Method method) {
        this.className = className;
        this.method = method;
    }

    public void show(PrintWriter out){
        out.println("Class: "+this.getClassName().getSimpleName());
        out.println("Methode: "+this.getMethod().getName());
    }
    
}