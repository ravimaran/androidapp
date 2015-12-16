package app.dev.sigtivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import app.dev.sigtivity.adapter.PhotoCommentAdapter;
import app.dev.sigtivity.core.CircleTransform;
import app.dev.sigtivity.core.PreferenceManager;
import app.dev.sigtivity.domain.Comment;
import app.dev.sigtivity.domain.ImageDetail;
import app.dev.sigtivity.domain.LikeLookup;
import app.dev.sigtivity.http.HttpManager;
import app.dev.sigtivity.http.RequestPackage;
import app.dev.sigtivity.parser.JSONParser;

public class ActivityImageDetail extends Activity {

    private ListView listViewComments;
    private EditText editTextComment;
    private List<Comment> userComments;
    private TextView txtCommentCount;
    private ImageView profileImage;
    private TextView txtProfileName;
    private TextView txtImgDate;
    private TextView txtLocationName;
    private TextView txtLikesCount;
    private TextView txtPhotoCaption;
    private ImageView imageView;
    private ImageView imgLiked;
    private ImageView imgUnLiked;
    private ProgressBar progressBar;

    private String userId;
    private int pictureId;
    private ImageDetail imageDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        //setting up image detail activity from xml
        setContentView(R.layout.activity_image_detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // initialize class variables and events
        initialize();
        // Load image detail
        new LoadImageDetail().execute();
    }

    private void initialize(){
        userId  = PreferenceManager.getUserId(this);
        imageView = (ImageView) findViewById(R.id.imgDetailView);
        profileImage = (ImageView)findViewById(R.id.profileImg);
        txtImgDate = (TextView)findViewById(R.id.txtImgDate);
        txtProfileName = (TextView) findViewById(R.id.txtProfileName);
        txtLocationName = (TextView)findViewById(R.id.txtLocationName);
        txtLikesCount = (TextView)findViewById(R.id.txtLikesCount);
        editTextComment = (EditText)findViewById(R.id.editTextComment);
        listViewComments = (ListView) findViewById(R.id.listViewComments);
        txtPhotoCaption = (TextView) findViewById(R.id.editTextPhotoCaption);
        txtCommentCount = (TextView) findViewById(R.id.txtCommentCounts);
        imgLiked = (ImageView) findViewById(R.id.imgLikedHeart);
        imgUnLiked = (ImageView) findViewById(R.id.imgUnLikedHeart);
        progressBar = (ProgressBar)findViewById(R.id.progressBarImageDetail);

        Bundle bundle = getIntent().getExtras();
        pictureId = bundle.getInt("picture_id");

        initializeEvents();
    }

    private void initializeEvents(){
        // Enable comment edit text to set focus
//        editTextComment.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    setFocusOnCommentEditText(true);
//                }
//
//                return false;
//            }
//        });

        // hide keyboard if set out of focus
        editTextComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        final GestureDetector gd = new GestureDetector(this, new MyGestureDetector());
        // double tab event to image
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gd.onTouchEvent(event);
                return false;
            }
        });
    }

    private void bindImageDetail(){
        txtProfileName.setText(imageDetail.getProfileName());
        txtLocationName.setText(imageDetail.getEventLocationName());
        txtCommentCount.setText(String.valueOf(imageDetail.getCommentCount()));
        txtLikesCount.setText(String.valueOf(imageDetail.getLikeCount()));
        txtImgDate.setText(imageDetail.getPhotoAddedDate());
        txtPhotoCaption.setText(imageDetail.getPhotoCaption());
        Picasso.with(this).load(imageDetail.getProfileImgUrl()).transform(new CircleTransform()).into(profileImage);
        Picasso.with(this).load(imageDetail.getImageUrl()).into(imageView);
        progressBar.setVisibility(View.GONE);
        new LoadComments().execute(getCommentsRequestPackage());
    }

    private RequestPackage getCommentsRequestPackage() {
        RequestPackage requestPackage = new RequestPackage();
        requestPackage.setUri(String.format("http://giftandevent.com/comment/getComments/%s", String.valueOf(pictureId)));
        return requestPackage;
    }
//
//    private void setFocusOnCommentEditText(boolean hasFocus){
//        editTextComment.setFocusableInTouchMode(hasFocus);
//        editTextComment.setFocusable(hasFocus);
//    }

    private static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        //int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, null, listView);
            view.measure(0, 0);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 70;
        listView.setLayoutParams(params);
    }

//    // overrides and public methods
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if(ev.getAction() == MotionEvent.ACTION_DOWN) {
//            View v = getCurrentFocus();
//            if (v instanceof EditText) {
//                setFocusOnCommentEditText(false);
//                hideKeyboard(v);
//            }
//        }
//
//        return super.dispatchTouchEvent(ev);
//    }

    public void addComment(View v){
        String commentText = String.valueOf(editTextComment.getText());
        if(commentText.length() > 0){
            RequestPackage requestPackage = new RequestPackage();
            requestPackage.setParam("user_id", userId);
            requestPackage.setParam("photo_id", Integer.toString(pictureId));
            requestPackage.setParam("comment", commentText);
            requestPackage.setParam("auth_token", "0");
            new AddAndRefreshComments().execute(requestPackage);
            editTextComment.setText("");
            //setFocusOnCommentEditText(false);
            hideKeyboard(v);
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void displayComments(){
        PhotoCommentAdapter commentAdapter = new PhotoCommentAdapter(getApplicationContext(), userComments);
        listViewComments.setAdapter(commentAdapter);
        setListViewHeightBasedOnChildren(listViewComments);
        txtCommentCount.setText(String.valueOf(listViewComments.getCount()));
    }

    private void AnimateLike(){
        Animation fadeout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_fade_out);
        fadeout.setFillAfter(true);
        fadeout.setDuration(3000);
        imgLiked.setVisibility(View.VISIBLE);
        imgLiked.setAnimation(fadeout);
        imgLiked.setVisibility(View.GONE);
    }

    private void AnimateUnLike(){
        Animation fadeout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_fade_out);
        fadeout.setFillAfter(true);
        fadeout.setDuration(3000);
        imgUnLiked.setVisibility(View.VISIBLE);
        imgUnLiked.setAnimation(fadeout);
        imgUnLiked.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_image_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    // internal classes
    private class LoadImageDetail extends  AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            return HttpManager.getPictureDetail(pictureId);
        }

        @Override
        protected void onPostExecute(String jsonString) {
            imageDetail = JSONParser.parseImageDetail(jsonString);
            bindImageDetail();
        }
    }

//    private class ImageViewLoader extends AsyncTask<String, Void, Bitmap> {
//        ImageView imgView;
//        boolean circularImage;
//        public ImageViewLoader(ImageView imgView, boolean circularImage){
//            this.imgView = imgView;
//            this.circularImage = circularImage;
//        }
//
//        @Override
//        protected Bitmap doInBackground(String... params) {
//            String pictureUrl = params[0];
//            try{
//                InputStream stream = (InputStream) new URL(pictureUrl).getContent();
//                Bitmap bitmap = BitmapFactory.decodeStream(stream);
//                stream.close();
//                return  bitmap;
//            }catch(Exception ex){
//                ex.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            if(circularImage){
//                loadCircularImage(bitmap);
//            }else{
//                loadNormalImage(bitmap);
//            }
//        }
//
//        private void loadNormalImage(Bitmap bitmap){
//            imgView.setImageBitmap(bitmap);
//        }
//
//        private void loadCircularImage(Bitmap bitmap){
//            CircularImage circularImage = new CircularImage(bitmap);
//            imgView.setImageDrawable(circularImage);
//        }
//    }

    private class AddAndRefreshComments extends AsyncTask<RequestPackage, String, String>{
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HttpManager.AddComent(params[0]);
        }

        @Override
        protected void onPostExecute(String jsonString) {
            userComments = JSONParser.parseComments(jsonString);
            displayComments();
        }
    }

    private class LoadComments extends AsyncTask<RequestPackage, String, String>{
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HttpManager.getData(params[0]);
        }

        @Override
        protected void onPostExecute(String jsonString) {
            userComments = JSONParser.parseComments(jsonString);
            displayComments();
        }
    }
//
//    public class LoadUser extends AsyncTask<RequestPackage, String, String>{
//        @Override
//        protected String doInBackground(RequestPackage... params) {
//            return HttpManager.getData(params[0]);
//        }
//
//        @Override
//        protected void onPostExecute(String jsonString) {
//            String btnPictureText = getResources().getString(R.string.profile_picture_count);
//            String btnEventText = getResources().getString(R.string.profile_event_count);
//            String btnNetworkText = getResources().getString(R.string.profile_networks_count);
//
//            user = JSONParser.parseUser(jsonString);
//            txtProfileName.setText(user.getName());
//            txtLocationName.setText(user.getTagLine());
//            new LoadProfileImage().execute(user.getProfileImgUrl());
//        }
//    }

//    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
//        @Override
//        protected Bitmap doInBackground(String... params) {
//            String pictureUrl = params[0];
//            try{
//                InputStream stream = (InputStream) new URL(pictureUrl).getContent();
//                Bitmap bitmap = BitmapFactory.decodeStream(stream);
//                stream.close();
//                return  bitmap;
//            }catch(Exception ex){
//                ex.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            CircularImage circularImage = new CircularImage(bitmap);
//            profileImage.setImageDrawable(circularImage);
//        }
//    }

    private class UpdateLikes extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            return HttpManager.updateLikes(userId, Integer.toString(pictureId));
        }

        @Override
        protected void onPostExecute(String s) {
            LikeLookup lookup = JSONParser.parseLikeLookup(s);
            txtLikesCount.setText(String.valueOf(lookup.getLikes()));
            if(lookup.isLiked()){
                AnimateLike();
            }else{
                AnimateUnLike();
            }
        }
    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            new UpdateLikes().execute();
            return super.onDoubleTap(e);
        }
    }
}
