package app.dev.sigtivity;

import android.app.Activity;
import android.app.DialogFragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.dev.sigtivity.domain.UserAuthentication;
import app.dev.sigtivity.http.HttpManager;
import app.dev.sigtivity.http.RequestPackage;
import app.dev.sigtivity.parser.JSONParser;
import app.dev.sigtivity.utils.CommonHelper;

public class FragmentSignIn extends DialogFragment {
    private OnFragmentInteractionListener mListener;
    private EditText userEmail;
    private EditText userPassword;
    private TextView loginError;
    private String email;
    private String password;
    private boolean isFormValid = true;

    public static FragmentSignIn newInstance() {
        return new FragmentSignIn();
    }

    public FragmentSignIn() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_signin, container, false);
        userEmail = (EditText)view.findViewById(R.id.txtEmail);
        userPassword = (EditText)view.findViewById(R.id.txtPassword);
        loginError = (TextView)view.findViewById(R.id.txtLoginErrMessage);

        final Button signInBtn = (Button)view.findViewById(R.id.btnFragementSignIn);
        signInBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                email = userEmail.getText().toString();
                password = userPassword.getText().toString();
                validateSignInForm();
                if(isFormValid) {
                    RequestPackage requestPackage = new RequestPackage();
                    requestPackage.setParam("email", userEmail.getText().toString());
                    requestPackage.setParam("password", userPassword.getText().toString());
                    requestPackage.setMethod("POST");
                    new loginUser().execute(requestPackage);
                }
            }
        });

        final ImageButton closeBtn = (ImageButton)view.findViewById(R.id.btnActionClose);
        closeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    private void validateSignInForm(){
        isFormValid = true;
        if(email.length() == 0){
            isFormValid = false;
            userEmail.setError("Email is required.");
        }

        if(email.length() > 0 && !CommonHelper.isEmailValid(email)){
            isFormValid = false;
            userEmail.setError("Invalid email format.");
        }

        if(password.length() == 0){
            isFormValid = false;
            userPassword.setError("Password is required.");
        }
    }

    class loginUser extends AsyncTask<RequestPackage, String, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HttpManager.getUserAuthData(params[0]);
        }

        @Override
        protected void onPostExecute(String content) {
            UserAuthentication userAuth = JSONParser.parseUserAuthentication(content);
            if(userAuth.isLoggedin()){
                if(mListener != null){
                    mListener.onFragmentInteraction(true, userAuth.getUserid());
                    dismiss();
                }
            }else{
                isFormValid = false;
                loginError.setText(userAuth.getMessage());
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(boolean success, int userId);
    }

}
