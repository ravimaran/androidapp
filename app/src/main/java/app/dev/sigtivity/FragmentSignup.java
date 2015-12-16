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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.dev.sigtivity.domain.RegisterValidation;
import app.dev.sigtivity.domain.UserAuthentication;
import app.dev.sigtivity.http.HttpManager;
import app.dev.sigtivity.http.RequestPackage;
import app.dev.sigtivity.parser.JSONParser;
import app.dev.sigtivity.utils.CommonHelper;

public class FragmentSignup extends DialogFragment {
    private ImageButton closeButton;
    private Button signUpButton;
    private EditText editTextEmail;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextPasswordConfirm;
    private String password;
    private String passwordConfirm;
    private String email;
    private String userName;
    private boolean formValid = true;

    private OnFragmentInteractionListener mListener;

    public static FragmentSignup newInstance() {
        return new FragmentSignup();
    }

    public FragmentSignup() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        closeButton = (ImageButton)view.findViewById(R.id.btnActionClose);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        editTextEmail = (EditText)view.findViewById(R.id.textEmail);
        editTextPassword = (EditText)view.findViewById(R.id.textPassword);
        editTextPasswordConfirm = (EditText)view.findViewById(R.id.textPasswordConfirm);
        editTextUsername = (EditText)view.findViewById(R.id.textUsername);

        signUpButton = (Button)view.findViewById(R.id.btnSignUp);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = String.valueOf(editTextPassword.getText());
                passwordConfirm = String.valueOf(editTextPasswordConfirm.getText());
                email = String.valueOf(editTextEmail.getText());
                userName = String.valueOf(editTextUsername.getText());
                validateUserInputs();
                if(formValid) {
                    RequestPackage requestPackage = new RequestPackage();
                    requestPackage.setUri("http://giftandevent.com/auth/validate/" + email + "/" + userName);
                    new ValidateUserNameAndEmail().execute(requestPackage);
                }
            }
        });

        return view;
    }

    private void validateUserInputs(){
        formValid = true;
        if(email.length() == 0){
            formValid = false;
            editTextEmail.setError("Email is required.");
        }

        if(email.length() > 0 && !CommonHelper.isEmailValid(email)){
            formValid = false;
            editTextEmail.setError("Invalid email format.");
        }

        if(userName.length() == 0){
            formValid = false;
            editTextUsername.setError("Username is required.");
        }

        if(password.length() == 0){
            formValid = false;
            editTextPassword.setError("Password is required.");
        }

        if(passwordConfirm.length() == 0){
            formValid = false;
            editTextPasswordConfirm.setError("Confirm password is required.");
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
        public void onFragmentInteraction(boolean success, int userid, String fragmentName);
    }

    public class ValidateUserNameAndEmail extends AsyncTask<RequestPackage, String, String>{
        RequestPackage requestPackage;
        @Override
        protected String doInBackground(RequestPackage... params) {
            requestPackage = params[0];
            return HttpManager.getData(params[0]);
        }

        @Override
        protected void onPostExecute(String jsonString) {
            formValid = true;
            RegisterValidation validation = JSONParser.parseRegisterValidation(jsonString);
            if(validation.getEmailExists()){
                formValid = false;
                editTextEmail.setError("Email already exists");
            }

            if(validation.getUsernameExists()){
                formValid = false;
                editTextUsername.setError("Username already exists");
            }

            if(!password.equals(passwordConfirm)){
                formValid = false;
                editTextPasswordConfirm.setError("Passwords do not match!");
            }

            if(formValid){
                requestPackage.setParam("username", userName);
                requestPackage.setParam("password", password);
                requestPackage.setParam("email", email);
                requestPackage.setParam("name", "");
                new RegisterUser().execute(requestPackage);
            }
        }
    }

    public class RegisterUser extends AsyncTask<RequestPackage, String, String>{
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HttpManager.registerUser(params[0]);
        }

        @Override
        protected void onPostExecute(String content) {
            UserAuthentication userAuth = JSONParser.parseUserAuthentication(content);
            if(mListener != null){
                mListener.onFragmentInteraction(true, userAuth.getUserid(), "registration");
                dismiss();
            }
        }
    }
}
