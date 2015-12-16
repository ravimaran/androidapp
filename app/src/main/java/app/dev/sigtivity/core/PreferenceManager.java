package app.dev.sigtivity.core;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ravi on 12/13/2015.
 */
public class PreferenceManager {
    public static void setLogInSuceess(boolean login, Context context) {
        getSharedPreference(context).edit().putBoolean(GlobalConstants.KEY_LOGIN, login).commit();
    }

    public static boolean getLogInSuceess(Context context){
        return getSharedPreference(context).getBoolean(GlobalConstants.KEY_LOGIN, false);
    }

    public static void setUserId(String userId, Context context){
        getSharedPreference(context).edit().putString(GlobalConstants.KEY_USER_ID, userId).commit();
    }

    public static String getUserId(Context context){
        return getSharedPreference(context).getString(GlobalConstants.KEY_USER_ID, null);
    }

    public static void clearPreference(Context context){
        getSharedPreference(context).edit().clear().commit();
    }

    private static SharedPreferences getSharedPreference(Context context){
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(GlobalConstants.SIG_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences;
    }
}
