package mg.itu.prom16.utilities;

import java.io.PrintWriter;

public class Mapping {
    String className;
    String methodName;
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public String getMethodName() {
        return methodName;
    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    public Mapping(String className, String methodName) {
        setClassName(className);
        setMethodName(methodName);
    }

    public void show(PrintWriter out){
        out.println("Class: "+this.getClassName());
        out.println("Methode: "+this.getMethodName());
    }
    
}