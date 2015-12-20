package app.dev.sigtivity.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import app.dev.sigtivity.ActivityMain;
import app.dev.sigtivity.CircularImage;
import app.dev.sigtivity.R;
import app.dev.sigtivity.domain.Photo;
import app.dev.sigtivity.helper.DateHelper;
import app.dev.sigtivity.http.HttpManager;
import app.dev.sigtivity.http.RequestPackage;

/**
 * Created by Ravi on 7/12/2015.
 */
public class HotSpotPhotoAdapter extends BaseAdapter {
    List<Photo> photos;
    Context context;

    public HotSpotPhotoAdapter(Context context, List<Photo> photos){
        this.context = context;
        this.photos = photos;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View convertView, final ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.template_feed_row, parent, false);
        final TextView caption = (TextView)view.findViewById(R.id.editTextPhotoCaption);
        final Photo photo = photos.get(position);
        TextView userNameTextView = (TextView) view.findViewById(R.id.hotSpotProfileName);
        userNameTextView.setText(photo.getUserName());
        view.setId(photo.getPictureId());
        TextView txtView = (TextView) view.findViewById(R.id.imageUrl);
        txtView.setText(photo.getImageUrl());

        TextView txtComments = (TextView) view.findViewById(R.id.txtCommentCount);
        TextView txtLikes = (TextView) view.findViewById(R.id.txtLikesCount);
        TextView txtImgDate = (TextView) view.findViewById(R.id.txtImgDate);

        caption.setText(photo.getPhotoCaption());
        txtComments.setText(String.valueOf(photo.getComments()));
        txtLikes.setText(String.valueOf(photo.getLikes()));
        txtImgDate.setText(DateHelper.getDateSpan(photo.getDateTaken()));
        ImageView imageView = (ImageView) view.findViewById(R.id.hotSpotImg);
        ImageView profileImgView = (ImageView) view.findViewById(R.id.hotSpotProfileImg);

        if(photo.getBitmap() != null){
            imageView.setImageBitmap(photo.getBitmap());
        }else{
            PhotoAndView pictureAndView = new PhotoAndView();
            pictureAndView.photo = photo;
            pictureAndView.view = view;
            new ImageLoader(imageView, false).execute(pictureAndView);
        }

        if(photo.getProfileBitmap() != null){
            profileImgView.setImageDrawable(photo.getProfileBitmap());
        }else{
            PhotoAndView pictureAndView = new PhotoAndView();
            pictureAndView.photo = photo;
            pictureAndView.view = view;
            new ImageLoader(profileImgView, true).execute(pictureAndView);
        }

        profileImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabHost host = ActivityMain.getCurrentTabHost();
                host.setCurrentTab(0);
            }
        });

        caption.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    RequestPackage requestPackage = new RequestPackage();
                    requestPackage.setMethod("POST");
                    requestPackage.setParam("photo_id", Integer.toString(photo.getPictureId()));
                    requestPackage.setParam("photo_caption", String.valueOf(caption.getText()));
                    new UpdateCaption().execute(requestPackage);
                }
            }
        });

        return view;
    }

    class PhotoAndView{
        public Photo photo;
        public View view;
        public Bitmap bitmap;
        public CircularImage profileBitmap;
    }

    private class ImageLoader extends AsyncTask<PhotoAndView, Void, PhotoAndView> {

        private ImageView imgView;
        private boolean isProfile;

        public ImageLoader(ImageView imgView, boolean isProfile){
            this.imgView = imgView;
            this.isProfile = isProfile;
        }
        @Override
        protected PhotoAndView doInBackground(PhotoAndView... params) {
            PhotoAndView container = params[0];
            Photo picture = container.photo;
            try{
                String url = picture.getImageUrl();
                if(isProfile){
                    url = picture.getProfileImgUrl();
                }

                InputStream stream = (InputStream) new URL(url).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                stream.close();
                if(bitmap != null) {
                    if(isProfile){
                        CircularImage circularImage = new CircularImage(bitmap);
                        container.profileBitmap = circularImage;
                    }else {
                        container.bitmap = bitmap; // Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), (int)(bitmap.getWidth() * 0.6));
                    }
                }

                return container;
            }catch(Exception ex){
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(PhotoAndView photoAndView) {
            if(isProfile){
                imgView.setImageDrawable(photoAndView.photo.getProfileBitmap());
                photoAndView.photo.setProfileBitmap(photoAndView.profileBitmap);
            }else {
                imgView.setImageBitmap(photoAndView.bitmap);
                photoAndView.photo.setBitmap(photoAndView.bitmap);
            }
        }
    }

    protected class UpdateCaption extends AsyncTask<RequestPackage, String, String>{
        @Override
        protected String doInBackground(RequestPackage... params) {
            HttpManager.updateCaption(params[0]);
            return null;
        }
    }
}
