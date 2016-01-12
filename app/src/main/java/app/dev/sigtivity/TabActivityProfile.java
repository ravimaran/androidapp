package app.dev.sigtivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.MissingResourceException;

import app.dev.sigtivity.adapter.UserPictureListAdapter;
import app.dev.sigtivity.core.CircleTransform;
import app.dev.sigtivity.core.GlobalConstants;
import app.dev.sigtivity.core.ImageManager;
import app.dev.sigtivity.core.PreferenceManager;
import app.dev.sigtivity.domain.Photo;
import app.dev.sigtivity.domain.User;
import app.dev.sigtivity.helper.CustomVolleyRequest;
import app.dev.sigtivity.helper.ImageHelper;
import app.dev.sigtivity.http.HttpManager;
import app.dev.sigtivity.http.RequestPackage;
import app.dev.sigtivity.loader.ProfileImageLoader;
import app.dev.sigtivity.parser.JSONParser;
import app.dev.sigtivity.utils.ConnectionManager;


public class TabActivityProfile extends Activity {
    GridView userPicturesGrid;
    List<Photo> userPhotos;
    private User user;
    private int profileId;
    private boolean readOnly = false;

    private TextView editTextProfileTitle;
    private TextView editTextProfileName;
    private ImageView profileImageView;

    //private String userId;
    private Button btnPictureCount;
    private Button btnEventCount;
    private Button btnNetworkCount;

    private FrameLayout profileFrameLayout;
    private int currentLayout = 1;
    private int previousLayout = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // check if internet connection exist
        if(!ConnectionManager.isOnline(getApplicationContext())){
            Toast.makeText(this, "You are not connected to internet!", Toast.LENGTH_LONG).show();
        }

        // initialize variables and view controls
        initialize();
        // load user info
        loadUserInfo();
        // load user pics frame layout as default
        loadFrameLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserInfo();
        new UserPicsLoader().execute(String.valueOf(profileId));
    }

    private void initialize(){
        setProfileId();
        //initiate profile controls
        editTextProfileName = (TextView)findViewById(R.id.txtViewProfileName);
        editTextProfileTitle = (TextView)findViewById(R.id.textViewProfileTitle);
        profileImageView = (ImageView)findViewById(R.id.imgViewProfile);
        btnPictureCount = (Button)findViewById(R.id.btnPictureCount);
        btnEventCount = (Button)findViewById(R.id.btnEventCount);
        btnNetworkCount = (Button) findViewById(R.id.btnNetworkCount);
        profileFrameLayout = (FrameLayout) findViewById(R.id.profileFrameLayout);
        userPicturesGrid = (GridView) findViewById(R.id.gridViewProfilePics);
    }

    private void loadUserInfo(){
        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setParam("user_id", String.valueOf(profileId));
        requestPackage.setUri(String.format("http://giftandevent.com/auth/profile/%d//0/", profileId));

        new LoadUser(this).execute(requestPackage);
    }

    private void loadFrameLayout(){
        boolean changeLayout = previousLayout != currentLayout;
        if(changeLayout) {
            profileFrameLayout.removeAllViews();
            switch (currentLayout) {
                case 2:
                    setSelectedStat(btnEventCount);
                    LayoutInflater.from(getApplicationContext()).inflate(R.layout.framelayout_profile_events, profileFrameLayout, true);
                    break;
                case 3:
                    setSelectedStat(btnNetworkCount);
                    LayoutInflater.from(getApplicationContext()).inflate(R.layout.framelayout_profile_networks, profileFrameLayout, true);
                    break;
                case 1:
                default:
                    setSelectedStat(btnPictureCount);
                    LayoutInflater.from(getApplicationContext()).inflate(R.layout.framelayout_profile_pictures, profileFrameLayout, true);
                    userPicturesGrid = (GridView) profileFrameLayout.findViewById(R.id.gridViewProfilePics);
                    new UserPicsLoader().execute(String.valueOf(profileId));
                    break;
            }
        }
    }

    private void setSelectedStat(Button clickedBtn){
        btnEventCount.setTypeface(null, Typeface.NORMAL);
        btnPictureCount.setTypeface(null, Typeface.NORMAL);
        btnNetworkCount.setTypeface(null, Typeface.NORMAL);

        btnEventCount.setTextColor(Color.BLACK);
        btnPictureCount.setTextColor(Color.BLACK);
        btnNetworkCount.setTextColor(Color.BLACK);

        clickedBtn.setTypeface(null, Typeface.BOLD);
        clickedBtn.setTextColor(Color.parseColor("#0099cc"));
    }

    private void setProfileId(){
        int userId = Integer.parseInt(PreferenceManager.getUserId(this));
        profileId = getIntent().getIntExtra(GlobalConstants.KEY_PROFILE_ID, userId);
        if(getIntent().hasExtra(GlobalConstants.KEY_PROFILE_ID)){
            readOnly = getIntent().getBooleanExtra(GlobalConstants.KEY_READ_ONLY, false);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void displayPictureGrid(){
        UserPictureListAdapter adapter = new UserPictureListAdapter(getApplicationContext(), userPhotos);
        userPicturesGrid.setAdapter(adapter);
        userPicturesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pictureId = view.getId();
                Intent i = new Intent().setClass(view.getContext(), ActivityImageDetail.class);
                i.putExtra(GlobalConstants.KEY_PICTURE_ID, pictureId);
                i.setAction(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                startActivity(i);
            }
        });
    }

    // Internal classes starts here
    public class UserPicsLoader extends  AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... params) {
            int id = Integer.parseInt(params[0]);
            return HttpManager.getUserPictures(id);
        }

        @Override
        protected void onPostExecute(String content) {
            userPhotos = app.dev.sigtivity.parser.JSONParser.parsePhotos(content);
            displayPictureGrid();
        }
    }

    public class LoadUser extends AsyncTask<RequestPackage, String, String>{
        Context context;

        public LoadUser(Context context){
            this.context = context;
        }

        @Override
        protected String doInBackground(RequestPackage... params) {
            return HttpManager.getData(params[0]);
        }

        @Override
        protected void onPostExecute(String jsonString) {
            String btnPictureText = getResources().getString(R.string.profile_picture_count);
            String btnEventText = getResources().getString(R.string.profile_event_count);
            String btnNetworkText = getResources().getString(R.string.profile_networks_count);

            user = JSONParser.parseUser(jsonString);
            editTextProfileName.setText(user.getName());
            editTextProfileTitle.setText(user.getTagLine());
            Picasso.with(context).load(user.getProfileImgUrl()).transform(new CircleTransform()).into(profileImageView);
            btnPictureCount.setText(String.format(btnPictureText, Integer.toString(user.getPictureCount())));
            btnEventCount.setText(String.format(btnEventText, Integer.toString(user.getEventCount())));
            btnNetworkCount.setText(String.format(btnNetworkText, Integer.toString(user.getNetworkCount())));
        }
    }
    // end of internal classes

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(!readOnly) {
            getMenuInflater().inflate(R.menu.menu_activity_profile, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        else {
            Intent i = new Intent().setClass(this, ActivitySettings.class);
            i.setAction(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(i);
        }

        return false;
    }

    // Stats click-able events
    public void displayEventsGrid(View v){
        previousLayout = currentLayout;
        currentLayout = 2;
        loadFrameLayout();
    }

    public void displayPicturesGrid(View v){
        previousLayout = currentLayout;
        currentLayout = 1;
        loadFrameLayout();
    }

    public void displayNetworks(View v){
        previousLayout = currentLayout;
        currentLayout = 3;
        loadFrameLayout();
    }

}
