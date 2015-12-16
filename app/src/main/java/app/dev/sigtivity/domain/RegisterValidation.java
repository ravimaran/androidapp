package app.dev.sigtivity.domain;

/**
 * Created by Ravi on 10/10/2015.
 */
public class RegisterValidation {
    private boolean usernameExists;
    private boolean emailExists;

    public void setUsernameExists(boolean usernameExists){
        this.usernameExists = usernameExists;
    }

    public void setEmailExists(boolean emailExists){
        this.emailExists = emailExists;
    }

    public boolean getUsernameExists(){return usernameExists;}
    public boolean getEmailExists(){return emailExists;}
}
