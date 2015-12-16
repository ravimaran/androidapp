package app.dev.sigtivity.helper;

import android.os.AsyncTask;

import app.dev.sigtivity.http.RequestPackage;

/**
 * Created by Ravi on 10/17/2015.
 */
public class AsyncHelper {
    private RequestPackage requestPackage;

    public AsyncHelper(RequestPackage requestPackage){
        this.requestPackage = requestPackage;
    }
}
