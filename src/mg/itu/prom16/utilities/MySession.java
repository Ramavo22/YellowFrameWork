package mg.itu.prom16.utilities;

import jakarta.servlet.http.HttpSession;

public class MySession {
    HttpSession session;

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public MySession() {}
    
    public MySession(HttpSession session){
        setSession(session);
    }

    // fonction 

    public Object get(String key){
        return getSession().getAttribute(key);
    }

    public void add(String key, Object value){
        getSession().setAttribute(key,value);
    }

    public void delete(String key){
        getSession().removeAttribute(key);
    }
    
}
