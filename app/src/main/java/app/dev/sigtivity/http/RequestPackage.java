package app.dev.sigtivity.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ravi on 7/11/2015.
 */
public class RequestPackage {
    private String uri;
    private String method = "GET";
    private Map<String, String> params = new HashMap<>();

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void setParam(String key, String value){
        this.params.put(key, value);
    }

    public String getParam(String key){
        return this.params.get(key).toString();
    }

    public String getEncodedParams(){
        StringBuilder sb = new StringBuilder();
        for(String key : params.keySet()){
            String value = "";
            try{
                value = URLEncoder.encode(params.get(key), "UTF-8");
            }catch(UnsupportedEncodingException ex){
                ex.printStackTrace();
            }

            if(sb.length() > 0){
                sb.append("&");
            }

            sb.append(key + "=" + value);
        }

        return sb.toString();
    }
}
