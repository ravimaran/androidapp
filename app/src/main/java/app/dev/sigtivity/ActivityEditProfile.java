package app.dev.sigtivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import app.dev.sigtivity.core.CircleTransform;
import app.dev.sigtivity.core.PreferenceManager;
import app.dev.sigtivity.helper.ImageHelper;
import app.dev.sigtivity.http.HttpManager;
import app.dev.sigtivity.http.RequestPackage;


public class ActivityEditProfile extends Activity{

    private ImageView imgViewProfile;
    private EditText txtProfileName;
    private EditText txtProfileEmail;
    private EditText txtProfileBio;
    private EditText txtProfilePhone;

    private static int RESULT_LOAD_IMAGE = 1;
    private String userId;
    private String profilePicPath = "";
    private File scaledFile;
    FileOutputStream stream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Initialize();
    }

    // region Actionbar and Menu items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save_profile:
                RequestPackage requestPackage = generatePostRequestPackage();
                updateUserProfile(requestPackage);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // end region

    // region overrides
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            profilePicPath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bm = ImageHelper.decodeSampledBitmapFromFile(profilePicPath, 200, 200); // BitmapFactory.decodeFile(picturePath);
            if(bm != null) {
                scaledFile = new File(getProfileImageFile().getAbsolutePath());
                try {
                    stream = new FileOutputStream(scaledFile.getAbsolutePath());
                    bm.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                    try{
                        stream.flush();
                        stream.close();
                    }catch (IOException ex){
                        ex.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Picasso.with(this).load(scaledFile).transform(new CircleTransform()).into(imgViewProfile);
            }else{
                Toast.makeText(this, "Picture couldn't save at this time!", Toast.LENGTH_LONG).show();
            }
        }
    }

    // region private methods
    private void Initialize(){
        userId = PreferenceManager.getUserId(this);
        txtProfileBio = (EditText)findViewById(R.id.txtProfileBio);
        txtProfileEmail = (EditText)findViewById(R.id.txtProfileEmail);
        txtProfileName = (EditText)findViewById(R.id.txtProfileName);
        txtProfilePhone = (EditText)findViewById(R.id.txtProfilePhone);
        imgViewProfile = (ImageView)findViewById(R.id.imgViewProfile);

        imgViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    private RequestPackage generatePostRequestPackage(){
        RequestPackage requestPackage = new RequestPackage();
        requestPackage.setParam("image_file_path", scaledFile.getAbsolutePath());
        requestPackage.setParam("user_id", userId);
        requestPackage.setParam("auth_token", "0");
        return requestPackage;
    }

    private void updateUserProfile(RequestPackage requestPackage){
        new UpdateProfile().execute(requestPackage);
    }

    private File getProfileImageFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Sigtivity");
        File profileFile = new File(mediaStorageDir.getPath() + File.separator + "Profile_IMG_" + java.util.UUID.randomUUID()  + ".jpg");
        return profileFile;
    }

    // region internal classes
    public class UpdateProfile extends AsyncTask<RequestPackage, String, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            HttpManager.uploadProfileImage(params[0]);
            return null;
        }
    }
}
