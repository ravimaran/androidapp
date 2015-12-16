package app.dev.sigtivity.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.dev.sigtivity.R;
import app.dev.sigtivity.domain.Photo;

/**
 * Created by Ravi on 7/10/2015.
 */
public class UserPictureListAdapter extends BaseAdapter {
    private List<Photo> userPhotos;
    private Context context;

    public UserPictureListAdapter(Context context, List<Photo> userPhotos){
        this.context = context;
        this.userPhotos = userPhotos;
    }

    @Override
    public int getCount() {
        return userPhotos.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.template_profile_pic_row, parent, false);
        Photo photo = userPhotos.get(position);
        view.setId(photo.getPictureId());
        ImageView imageView = (ImageView) view.findViewById(R.id.takenImg);
        Picasso.with(context).load(photo.getThumbnail()).into(imageView);
//        if(photo.getBitmap() != null){
//            ImageView imageView = (ImageView) view.findViewById(R.id.takenImg);
//            imageView.setImageBitmap(photo.getBitmap());
//        }else{
//            PhotoAndView pictureAndView = new PhotoAndView();
//            pictureAndView.photo = photo;
//            pictureAndView.view = view;
//            new ImageLoader().execute(pictureAndView);
//        }

        return view;
    }

    class PhotoAndView{
        public Photo photo;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<PhotoAndView, Void, PhotoAndView>{

        @Override
        protected PhotoAndView doInBackground(PhotoAndView... params) {
            PhotoAndView container = params[0];
            Photo picture = container.photo;
            try{
                InputStream stream = (InputStream) new URL(picture.getImageUrl()).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                stream.close();
                container.bitmap = bitmap;
                return container;
            }catch(Exception ex){
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(PhotoAndView photoAndView) {
            ImageView imageView = (ImageView) photoAndView.view.findViewById(R.id.takenImg);
            imageView.setImageBitmap(photoAndView.bitmap);
            photoAndView.photo.setBitmap(photoAndView.bitmap);
        }
    }
}
