package app.dev.sigtivity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.app.DialogFragment;
import android.widget.TextView;

import app.dev.sigtivity.http.RequestPackage;

public class FragmentEventCode extends DialogFragment {
    private OnFragmentInteractionListener mListener;
    private EditText eventCode;
    private String eventCodeText;

    public static FragmentEventCode newInstance() {
        FragmentEventCode fragment = new FragmentEventCode();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentEventCode() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_event_code, container, false);
        eventCode = (EditText)view.findViewById(R.id.txtEventCode);
        Button btnAcceptEventCode = (Button)view.findViewById(R.id.btnAcceptEventCode);
        btnAcceptEventCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventCodeText = eventCode.getText().toString();
                if (mListener != null) {
                    mListener.onFragmentInteraction(eventCodeText);
                    dismiss();
                }
            }
        });

        TextView locationMsg = (TextView)view.findViewById(R.id.txtLocationMsg);
        GPSTracker mGPS = new GPSTracker(this.getActivity());
        if(mGPS.canGetLocation()){
            mGPS.getLocation();
            locationMsg.setText(String.valueOf(mGPS.getLatitude()));
        }else{
            locationMsg.setText("Unable to find location");
        }

        return  view;
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
        public void onFragmentInteraction(String eventCode);
    }

}
