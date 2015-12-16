package app.dev.sigtivity.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import app.dev.sigtivity.CircularNetworkImageView;
import app.dev.sigtivity.R;
import app.dev.sigtivity.core.CircleTransform;
import app.dev.sigtivity.domain.Photo;
import app.dev.sigtivity.helper.CustomVolleyRequest;
import app.dev.sigtivity.helper.DateHelper;
import app.dev.sigtivity.loader.ProfileImageLoader;

/**
 * Created by Ravi on 12/2/2015.
 */
public class SigRecyclerViewAdapter extends RecyclerView.Adapter<SigRecyclerViewAdapter.DataObjectHolder> {

    private List<Photo> mDataset;
    private static SigClickListener sigClickListener;
    private ImageLoader imageLoader;
    private Context context;
    private ProfileImageLoader profileImageLoader;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        NetworkImageView imgView;
        ImageView profileImgView;
        TextView caption;
        TextView userNameTextView;
        TextView txtView;
        TextView txtComments;
        TextView txtLikes;
        TextView txtImgDate;


        public DataObjectHolder(View itemView){
            super(itemView);
            imgView = (NetworkImageView)itemView.findViewById(R.id.hotSpotImg);
            profileImgView = (ImageView)itemView.findViewById(R.id.hotSpotProfileImg);
            caption = (TextView)itemView.findViewById(R.id.editTextPhotoCaption);
            userNameTextView = (TextView)itemView.findViewById(R.id.hotSpotProfileName);
            txtView = (TextView)itemView.findViewById(R.id.imageUrl);
            txtComments = (TextView)itemView.findViewById(R.id.txtCommentCount);
            txtLikes = (TextView)itemView.findViewById(R.id.txtLikesCount);
            txtImgDate = (TextView)itemView.findViewById(R.id.txtImgDate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            sigClickListener.onItemClick(getPosition(), v);
        }
    }

    public void setOnItemClickListner(SigClickListener sigClickListener){
        this.sigClickListener = sigClickListener;
    }

    public SigRecyclerViewAdapter(List<Photo> mDataset, Context context){
        this.mDataset = mDataset;
        this.context = context;
        profileImageLoader = new ProfileImageLoader(context);
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.template_feed_row, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        //holder.imgView.setImageDrawable(null);
        Photo photo = mDataset.get(position);
        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(photo.getImageUrl(), ImageLoader.getImageListener(holder.imgView, R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert));
        holder.imgView.setImageUrl(photo.getImageUrl(), imageLoader);
//        imageLoader.get(photo.getProfileImgUrl(), ImageLoader.getImageListener(holder.profileImgView, R.drawable.ic_default_profile, R.drawable.ic_default_profile));
//        holder.profileImgView.setImageUrl(photo.getProfileImgUrl(), imageLoader);
        Picasso.with(context).load(photo.getProfileImgUrl()).transform(new CircleTransform()).into(holder.profileImgView);
        holder.caption.setText(photo.getPhotoCaption());
        holder.userNameTextView.setText(photo.getUserName());
        holder.txtView.setText(photo.getImageUrl());
        holder.txtComments.setText(String.valueOf(photo.getComments()));
        holder.txtLikes.setText(String.valueOf(photo.getLikes()));
        String timeSpan = DateHelper.getDateSpan(photo.getDateTaken());
        holder.txtImgDate.setText(timeSpan);
    }

    public void addItem(Photo photo, int index){
        mDataset.add(photo);
        notifyItemInserted(index);
    }

    public void deleteItem(int index){
        mDataset.remove(index);
        notifyItemRemoved(index);
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface SigClickListener {
        void onItemClick(int position, View v);
    }

}
