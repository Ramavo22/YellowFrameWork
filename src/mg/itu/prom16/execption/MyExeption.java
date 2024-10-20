package mg.itu.prom16.execption;

public class MyExeption extends Exception{
    int statusCode;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public MyExeption(String message,int statusCode){
        super(message);
        setStatusCode(statusCode);
    }
}
