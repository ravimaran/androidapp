package app.dev.sigtivity;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import app.dev.sigtivity.domain.EventDetail;
import app.dev.sigtivity.http.HttpManager;
import app.dev.sigtivity.parser.JSONParser;

/**
 * Created by Ravi on 12/16/2015.
 */
public class FragmentValidateEventCode extends Fragment {

    // Listener interface to be implemented
    public interface OnEventCodeFragmentInteractionListener {
        public void onEventCodeFragmentInteraction(EventDetail eventDetail);
    }

    private OnEventCodeFragmentInteractionListener mListener;

    private String latitude;
    private String longitude;
    private String eventCode;

    private TextView txtEventTitle;
    private TextView txtEventLocationName;
    private TextView txtParticipantsCount;
    private TextView txtTotalPhotosCount;
    private EditText txtEventCode;
    private Button btnJoinEvent;

    private EventDetail eventDetail;

    public FragmentValidateEventCode() {
        // Required empty public constructor
    }

    // Must set these values
    public void setCordinates(String latitude, String longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.framelayout_hotspot_eventcode, container, false);
        initializeEventCodeLayout(view);
        new EventFinder().execute();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnEventCodeFragmentInteractionListener) activity;
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

    private void initializeEventCodeLayout(View view){
        txtEventTitle = (TextView) view.findViewById(R.id.txtEventTitle);
        txtEventLocationName = (TextView) view.findViewById(R.id.txtEventLocationName);
        txtEventCode = (EditText) view.findViewById(R.id.txtEventCode);
        txtParticipantsCount = (TextView) view.findViewById(R.id.txtParticipantsCount);
        txtTotalPhotosCount = (TextView) view.findViewById(R.id.txtTotalPhotosCount);
        btnJoinEvent = (Button) view.findViewById(R.id.btnJoinEvent);

        btnJoinEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                validateAndProcessEvent();
            }
        });
    }

    private void bindEventDetail(){
        txtEventTitle.setText(eventDetail.getEventName());
        txtEventLocationName.setText(eventDetail.getLocationName());
        txtTotalPhotosCount.setText(String.format("%s photo(s)", String.valueOf(eventDetail.getTotalPhotos())));
        txtParticipantsCount.setText(String.format("%s joined", String.valueOf(eventDetail.getParticipants())));
        eventCode = eventDetail.getEventCode();
    }

    private void validateAndProcessEvent(){
        if(isValidEventCode()){
            if(mListener != null){
                mListener.onEventCodeFragmentInteraction(eventDetail);
            }
        }else{
            txtEventCode.setError("Invalid event code entered!");
        }
    }

    private boolean isValidEventCode(){
        return txtEventCode.getText().toString().equals(eventCode);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private class EventFinder extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            return HttpManager.getEventDetailByCordiates(String.valueOf(latitude), String.valueOf(longitude));
        }

        @Override
        protected void onPostExecute(String jsonString) {
            eventDetail = JSONParser.parseEventDetail(jsonString);
            bindEventDetail();
        }
    }
}
