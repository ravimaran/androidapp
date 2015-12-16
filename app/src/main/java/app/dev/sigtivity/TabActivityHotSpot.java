package app.dev.sigtivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import app.dev.sigtivity.adapter.HotSpotPhotoAdapter;
import app.dev.sigtivity.adapter.UserPictureListAdapter;
import app.dev.sigtivity.core.PreferenceManager;
import app.dev.sigtivity.domain.EventDetail;
import app.dev.sigtivity.domain.Photo;
import app.dev.sigtivity.http.HttpManager;
import app.dev.sigtivity.http.RequestPackage;
import app.dev.sigtivity.parser.JSONParser;


public class TabActivityHotSpot extends Activity{

    private enum CurrentView{
        ListView,
        GridView
    }

    private SharedPreferences sharedPreferences;
    private String userId;
    private ListView hotSpotListView;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private GridView hotSpotGridView;
    private ImageButton imgBtnGridView;
    private ImageButton imgBtnListView;
    private CurrentView currentView;
    private CurrentView clickedView;

    private Uri fileUri;
    private int eventId;
    private String eventCode;

    private EventDetail eventDetail;

    private List<Photo> eventPhotos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_spot);
        // get shared preference
        initialize();
        // declare camera button event
        Button cameraButton = (Button)findViewById(R.id.buttonCamera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                getParent().startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        DialogFragment eventCode = FragmentEventCode.newInstance();
//        eventCode.show(ft, "eventCodeFragment");

        //initialize view
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Bitmap scaledBitmap = DecodeSampledBitmapFromFile(fileUri.getPath(), 600, 600);
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
                        requestPackage.setParam("event_id", "1");
                        requestPackage.setParam("user_id", userId);
                        requestPackage.setParam("photo_caption", "");
                        requestPackage.setParam("image_file_path", scaledFile.getAbsolutePath());
                        new UploadPicture().execute(requestPackage);

                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_activity_hot_spot, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void initialize(){
        userId  = PreferenceManager.getUserId(this);
        //hotSpotListView = (ListView)findViewById(R.id.hotSpotList);
        hotSpotGridView = (GridView)findViewById(R.id.gridViewHotSpot);
        imgBtnGridView = (ImageButton)findViewById(R.id.imgBtnHotSpotGridView);
        imgBtnListView = (ImageButton)findViewById(R.id.imgBtnHotSpotListView);

//        Bundle bundle = getIntent().getExtras();
//        eventId = bundle.getInt("event_id");
//        eventCode = bundle.getString("event_code");
    }

    private void processEventDetail(){
        new LoadEvenPhotos().execute(Integer.toString(eventId));
    }

    private void displayEventPhotos(){
        if(currentView == CurrentView.ListView) {
            hotSpotGridView.setVisibility(View.GONE);
            hotSpotListView.setVisibility(View.VISIBLE);
            HotSpotPhotoAdapter adapter = new HotSpotPhotoAdapter(getApplicationContext(), eventPhotos);
            hotSpotListView.setAdapter(adapter);
            hotSpotListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        if(currentView == CurrentView.GridView){
            hotSpotGridView.setVisibility(View.VISIBLE);
            hotSpotListView.setVisibility(View.GONE);
            UserPictureListAdapter adapter = new UserPictureListAdapter(getApplicationContext(), eventPhotos);
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

    private Bitmap DecodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight){
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

    // internal classes
    private class AddUserToEvent extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            return HttpManager.getEventDetail(String.valueOf(userId), String.valueOf(eventId), "0", eventCode);
        }

        @Override
        protected void onPostExecute(String content) {
            eventDetail = JSONParser.parseEventDetail(content);
            processEventDetail();
        }
    }

    class LoadEvenPhotos extends AsyncTask<String, String, String> {
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

    class UploadPicture extends AsyncTask<RequestPackage, String, String>{
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
