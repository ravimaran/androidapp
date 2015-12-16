package app.dev.sigtivity.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Ravi on 7/10/2015.
 */
public class ConnectionManager {
    public static boolean isOnline(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info != null && info.isConnectedOrConnecting()){
            return  true;
        }else{
            return  false;
        }
    }
}
