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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import app.dev.sigtivity.CircularImage;
import app.dev.sigtivity.R;
import app.dev.sigtivity.core.CircleTransform;
import app.dev.sigtivity.domain.Comment;
import app.dev.sigtivity.domain.Photo;
import app.dev.sigtivity.http.HttpManager;
import app.dev.sigtivity.http.RequestPackage;

/**
 * Created by Ravi on 7/12/2015.
 */
public class PhotoCommentAdapter extends BaseAdapter {
    List<Comment> comments;
    Context context;

    public PhotoCommentAdapter(Context context, List<Comment> comments){
        this.context = context;
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.size();
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
        View view = inflater.inflate(R.layout.template_comment_row, parent, false);
        TextView txtViewComment = (TextView)view.findViewById(R.id.txtViewComment);
        TextView txtViewTime = (TextView)view.findViewById(R.id.txtViewTime);
        Comment comment = comments.get(position);
        txtViewComment.setText(comment.getUserComment());
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String dateAdded = dateFormat.format(comment.getDateAdded());
        txtViewTime.setText(dateAdded);
        ImageView imageView = (ImageView) view.findViewById(R.id.hotSpotItemImg);
        //new LoadUserImage(imageView).execute(comment.getUserProfileImage());
        Picasso.with(context).load(comment.getUserProfileImage()).transform(new CircleTransform()).into(imageView);
        return view;
    }

    private class LoadUserImage extends AsyncTask<String, Void, Bitmap> {
        private ImageView imgView;
        public LoadUserImage(ImageView imgView){
            this.imgView = imgView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String pictureUrl = params[0];
            try{
                InputStream stream = (InputStream) new URL(pictureUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                stream.close();
                return  bitmap;
            }catch(Exception ex){
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            CircularImage circularImage = new CircularImage(bitmap);
            imgView.setImageDrawable(circularImage);
        }
    }
}
