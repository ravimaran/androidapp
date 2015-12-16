package app.dev.sigtivity.core;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import app.dev.sigtivity.R;
import app.dev.sigtivity.helper.CustomVolleyRequest;

/**
 * Created by Ravi on 12/13/2015.
 */
public class ImageManager {
    public static void setVolleyImage(String imageUrl, NetworkImageView imageView, Context context){
        ImageLoader imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(imageUrl, ImageLoader.getImageListener(imageView, R.drawable.ic_default_profile, R.drawable.ic_default_profile));
        imageView.setImageUrl(imageUrl, imageLoader);
    }
}
