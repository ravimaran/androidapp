package app.dev.sigtivity;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import app.dev.sigtivity.adapter.SigRecyclerViewAdapter;
import app.dev.sigtivity.adapter.UserPictureListAdapter;
import app.dev.sigtivity.core.GlobalConstants;
import app.dev.sigtivity.core.PreferenceManager;
import app.dev.sigtivity.domain.EventDetail;
import app.dev.sigtivity.domain.Photo;
import app.dev.sigtivity.http.HttpManager;
import app.dev.sigtivity.http.RequestPackage;
import app.dev.sigtivity.parser.JSONParser;

/**
 * Created by Ravi on 12/16/2015.
 */

public class FragmentLoadHotSpotFeed extends Fragment {
//    @Override
//    public void onFragmentInteraction(String caption) {
//        saveTakenPhoto(caption);
//    }

    public enum CurrentView{
        ListView,
        GridView
    }

    private OnHotSpotFragmentInteractionListener mListener;
    private TabActivityHotSpot context;
    private String userId;
    private int eventId;
    private EventDetail eventDetail;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private GridView hotSpotGridView;
    private ImageButton imgBtnGridView;
    private ImageButton imgBtnListView;
    private CurrentView currentView;
    private CurrentView clickedView;
    private Uri fileUri;
    private Button cameraButton;
    private List<Photo> eventPhotos;
    private Bitmap scaledBitmap;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    // Must have empty constructor
    public FragmentLoadHotSpotFeed(){}

    public void setCurrentActivityAndEvent(TabActivityHotSpot context, EventDetail eventDetail){
        this.context = context;
        this.eventDetail = eventDetail;
    }

    public void updateTakenPhoto(String photoCaption, Bitmap scaledBitmap){
        this.scaledBitmap = scaledBitmap;
        saveTakenPhoto(photoCaption);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_hot_spot, container, false);
        initializeHotSpotFeed(view);
//        initializeEventCodeLayout(view);
//        new EventFinder().execute();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnHotSpotFragmentInteractionListener) activity;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == context.RESULT_OK){
                scaledBitmap = decodeSampledBitmapFromFile(fileUri.getPath(), 600, 600);
                try {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    DialogFragment photoCaption = FragmentPhotoCaption.newInstance(scaledBitmap);
                    ft.add(photoCaption, null);
                    ft.commitAllowingStateLoss();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnHotSpotFragmentInteractionListener {
        public void onHotSpotFragmentInteraction();
    }

    private void initializeHotSpotFeed(View view){
        ActivityMain parent = (ActivityMain)context.getParent();
        parent.showActionBar(eventDetail.getEventName());
        userId = PreferenceManager.getUserId(context);
        eventId = eventDetail.getEventId();
        //hotSpotListView = (ListView)hotSpotLayout.findViewById(R.id.hotSpotList);
        hotSpotGridView = (GridView)view.findViewById(R.id.gridViewHotSpot);
        imgBtnGridView = (ImageButton)view.findViewById(R.id.imgBtnHotSpotGridView);
        imgBtnListView = (ImageButton)view.findViewById(R.id.imgBtnHotSpotListView);
        cameraButton = (Button)view.findViewById(R.id.buttonCamera);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.sig_recycler_view);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                context.setFileUri(fileUri);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                getActivity().startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        currentView = CurrentView.ListView;
        clickedView = CurrentView.ListView;
        hotSpotGridView.setVisibility(View.GONE);
        imgBtnGridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedView = CurrentView.GridView;
                if(clickedView != currentView) {
                    currentView = currentView.GridView;
                    new LoadEvenPhotos().execute(Integer.toString(eventId));
                }
            }
        });

        imgBtnListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedView = CurrentView.ListView;
                if(clickedView != currentView) {
                    currentView = CurrentView.ListView;
                    new LoadEvenPhotos().execute(Integer.toString(eventId));
                }
            }
        });

        new AddUserToEvent().execute();
    }

    private void processEventDetail(){
        new LoadEvenPhotos().execute(Integer.toString(eventId));
    }

    private void displayEventPhotos(){
        if(currentView == CurrentView.ListView) {
            hotSpotGridView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter = new SigRecyclerViewAdapter(eventPhotos, context);
            ((SigRecyclerViewAdapter)mAdapter).setOnItemClickListner(new SigRecyclerViewAdapter.SigClickListener(){
                @Override
                public void onItemClick(int position, View v, int elementId) {
                    if(R.id.hotSpotImg == v.getId()){
                        Intent i = new Intent().setClass(v.getContext(), ActivityImageDetail.class);
                        i.putExtra(GlobalConstants.KEY_PICTURE_ID, elementId);
                        i.setAction(Intent.ACTION_MAIN);
                        i.addCategory(Intent.CATEGORY_LAUNCHER);
                        startActivity(i);
                    }else{
                        Intent i = new Intent().setClass(v.getContext(), TabActivityProfile.class);
                        i.putExtra(GlobalConstants.KEY_PROFILE_ID, elementId);
                        i.setAction(Intent.ACTION_MAIN);
                        i.addCategory(Intent.CATEGORY_LAUNCHER);
                        startActivity(i);
                    }
                }
            });

            mRecyclerView.setAdapter(mAdapter);
        }

        if(currentView == CurrentView.GridView){
            hotSpotGridView.setVisibility(View.VISIBLE);
            //hotSpotListView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            UserPictureListAdapter adapter = new UserPictureListAdapter(context.getApplicationContext(), eventPhotos);
            hotSpotGridView.setAdapter(adapter);
            hotSpotGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int pictureId = view.getId();
                    TextView textView = (TextView) view.findViewById(R.id.imageUrl);
                    Intent i = new Intent().setClass(view.getContext(), ActivityImageDetail.class);
                    i.putExtra("picture_id", pictureId);
                    i.putExtra("picture_url", textView.getText());
                    i.setAction(Intent.ACTION_MAIN);
                    i.addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(i);
                }
            });
        }
    }

    private void refreshAndDisplayEventPhotos(){
        new LoadEvenPhotos().execute(Integer.toString(eventId));
    }

    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Sigtivity");
        if(!mediaStorageDir.exists()) {
            if(!mediaStorageDir.mkdir()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if(type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        }else{
            return null;
        }

        return mediaFile;
    }

    private Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = (int)(Math.round((float) height / (float) reqHeight));
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            inSampleSize = (int)(Math.round((float) width / (float) reqWidth));
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap scaled = BitmapFactory.decodeFile(path, options);

        double newHeight = options.outHeight;
        double newWidth = options.outHeight;
        double ratio;
        if (options.outWidth > options.outHeight)
        {
            newHeight = reqHeight;
            ratio = (double)reqHeight / (double)options.outHeight;
            newWidth = options.outWidth * ratio;
        }
        else
        {
            newWidth = reqWidth;
            ratio = (double)reqWidth / (double)options.outWidth;
            newWidth = options.outHeight * ratio;
        }

        Matrix matrix = new Matrix();
        try {
            ExifInterface oldexif = new ExifInterface(path);
            int rotation = getRotation(oldexif);
            if (rotation != 0f) {
                matrix.preRotate(rotation);
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        Bitmap rescaled = Bitmap.createScaledBitmap(scaled, (int) newWidth, (int) newHeight, true);
        int x = rescaled.getWidth() / 2 - reqWidth / 2;
        int y = rescaled.getHeight() / 2 - reqHeight / 2;
        return Bitmap.createBitmap(rescaled, x, y, reqWidth, reqHeight, matrix, true);
    }

    private int getRotation(ExifInterface exif)
    {
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
        int rotate = 0;
        switch (orientation) {
            case 3:
                rotate = 180;
                break;
            case 6:
                rotate = 90;
                break;
            case 8:
                rotate = 270;
                break;
        }

        return rotate;
    }

    private void saveTakenPhoto(String caption) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Sigtivity");
        File scaledFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + java.util.UUID.randomUUID()  + ".jpg");
        try {
            FileOutputStream stream = new FileOutputStream(scaledFile.getAbsolutePath());
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            try {
                stream.flush();
                stream.close();
                //delete the original to save space
                File original = new File(fileUri.getPath());
                original.delete();

                RequestPackage requestPackage = new RequestPackage();
                requestPackage.setParam("auth_token", "0");
                requestPackage.setParam("event_id", String.valueOf(eventId));
                requestPackage.setParam("user_id", userId);
                requestPackage.setParam("photo_caption", caption);
                requestPackage.setParam("image_file_path", scaledFile.getAbsolutePath());
                new UploadPicture().execute(requestPackage);

            }catch(IOException e){
                e.printStackTrace();
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

//    private void hideKeyboard(View view) {
//        InputMethodManager inputMethodManager =(InputMethodManager)view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }

    // internal classes
    private class AddUserToEvent extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return HttpManager.getEventDetail(String.valueOf(userId), String.valueOf(eventDetail.getEventId()), "0", eventDetail.getEventCode());
        }

        @Override
        protected void onPostExecute(String content) {
            eventDetail = JSONParser.parseEventDetail(content);
            processEventDetail();
        }
    }

    private class LoadEvenPhotos extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            return HttpManager.getEventPhotosData(params[0]);
        }

        @Override
        protected void onPostExecute(String content) {
            eventPhotos = JSONParser.parsePhotos(content);
            displayEventPhotos();
        }
    }

    private class UploadPicture extends AsyncTask<RequestPackage, String, String>{
        @Override
        protected String doInBackground(RequestPackage... params) {
            HttpManager.uploadFile(params[0]);
            return null;
        }

        protected void onPostExecute(String content){
            refreshAndDisplayEventPhotos();
        }
    }
}
