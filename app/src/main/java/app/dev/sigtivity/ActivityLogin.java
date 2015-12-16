package app.dev.sigtivity;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import app.dev.sigtivity.core.PreferenceManager;
import app.dev.sigtivity.utils.ConnectionManager;

public class ActivityLogin extends Activity implements FragmentSignIn.OnFragmentInteractionListener, FragmentSignup.OnFragmentInteractionListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActionBar().hide();
        if(!ConnectionManager.isOnline(getApplicationContext())){
            Toast.makeText(this, "You are not connected to internet!", Toast.LENGTH_LONG).show();
        }

        if(isUserLoggedIn()){
            Intent intent = new Intent().setClass(this, ActivityMain.class);
            startActivity(intent);
        }else{
            final Button button = (Button)findViewById(R.id.btnSignIn);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    DialogFragment sigin = FragmentSignIn.newInstance();
                    sigin.show(ft, "signinFragment");
                }
            });

            final Button signUpBtn = (Button)findViewById(R.id.btnSignUp);
            signUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    DialogFragment signup = FragmentSignup.newInstance();
                    signup.show(ft, "signupFragment");
                }
            });
        }
    }

    private boolean isUserLoggedIn(){
        String userId = PreferenceManager.getUserId(this);
        return userId != null && userId.length() > 0 && !userId.equals("0");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(boolean success, int userId) {
        setLoginPreference(success, userId);
        Intent intent = new Intent().setClass(this, ActivityMain.class);
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(boolean success, int userId, String fragment) {
        setLoginPreference(success, userId);
        Intent intent = new Intent().setClass(this, ActivityMain.class);
        startActivity(intent);
    }

    private void setLoginPreference(boolean login, int userId){
        PreferenceManager.setUserId(String.valueOf(userId), this);
        PreferenceManager.setLogInSuceess(login, this);
    }
}
