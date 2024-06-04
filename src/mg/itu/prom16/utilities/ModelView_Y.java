package mg.itu.prom16.utilities;

import java.util.HashMap;

public class ModelView_Y {
    String url;
    HashMap<String,Object> data;
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public HashMap<String, Object> getData() {
        return data;
    }
    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public void addObject(String url, Object data){
        getData().put(url, data);
    }

    public ModelView_Y(String url){
        setUrl(url);
        data = new HashMap<>();
    }
    public ModelView_Y(){}
}
