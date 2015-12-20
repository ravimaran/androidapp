package app.dev.sigtivity;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class FragmentPhotoCaption extends DialogFragment {
    private static Bitmap photo;
    private EditText caption;
    private Button btnSaveCaption;
    private ImageView takenImage;
    private OnPhotoCaptionFragmentInteractionListener mListener;

    public static FragmentPhotoCaption newInstance(Bitmap takenPhoto) {
        photo = takenPhoto;
        FragmentPhotoCaption fragment = new FragmentPhotoCaption();
        return fragment;
    }

    public FragmentPhotoCaption() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_addcaption, container, false);
        takenImage = (ImageView) view.findViewById(R.id.imgTakenPhoto);
        caption = (EditText) view.findViewById(R.id.txtCaption);
        btnSaveCaption = (Button) view.findViewById(R.id.btnSaveCaption);

        setEditCaptionTextFocus(false);
        initializeEvents();
        takenImage.setImageBitmap(photo);
        return  view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPhotoCaptionFragmentInteractionListener) activity;
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


    public interface OnPhotoCaptionFragmentInteractionListener {
        void onPhotoCaptionFragmentInteraction(String caption);
    }

    private void setEditCaptionTextFocus(boolean editCaptionTextFocus) {
        caption.setFocusable(editCaptionTextFocus);
        caption.setFocusableInTouchMode(editCaptionTextFocus);
    }

    private void initializeEvents() {
        caption.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    setEditCaptionTextFocus(true);
                }

                return false;
            }
        });

        btnSaveCaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    String txtCaption = caption.getText().toString();
                    mListener.onPhotoCaptionFragmentInteraction(txtCaption);
                    dismiss();
                }
            }
        });
    }
}
