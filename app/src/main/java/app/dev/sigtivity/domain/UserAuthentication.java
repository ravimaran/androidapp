package app.dev.sigtivity.domain;

/**
 * Created by Ravi on 7/11/2015.
 */
public class UserAuthentication {
    private boolean loggedin;
    private String message;
    private int userid;
    private String authToken;

    public boolean isLoggedin() {
        return loggedin;
    }

    public void setUserid(int userid){this.userid = userid;}
    public void setLoggedin(boolean loggedin) {
        this.loggedin = loggedin;
    }
    public void setAuthToken(String authToken){this.authToken = authToken;}

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    public int getUserid(){return  this.userid;}

    public String getAuthToken(){return authToken;}
}
