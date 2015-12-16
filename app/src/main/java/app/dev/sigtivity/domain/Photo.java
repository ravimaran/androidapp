package app.dev.sigtivity.domain;

import android.graphics.Bitmap;

import java.util.Date;

import app.dev.sigtivity.CircularImage;

/**
 * Created by Ravi on 7/10/2015.
 */
public class Photo {
    private int pictureId;
    private String imageUrl;
    private String userName;
    private String photoName;
    private String photoCaption;
    private String profileImgUrl;
    private String thumbnail;
    private Date dateTaken;
    private Bitmap bitmap;
    private CircularImage profileBitmap;

    private int comments;
    private int likes;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPictureId() {
        return pictureId;
    }

    public void setPictureId(int pictureId) {
        this.pictureId = pictureId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getPhotoCaption() {
        return photoCaption;
    }

    public void setPhotoCaption(String photoCaption) {
        this.photoCaption = photoCaption;
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public CircularImage getProfileBitmap() {
        return profileBitmap;
    }

    public void setProfileBitmap(CircularImage profileBitmap) {
        this.profileBitmap = profileBitmap;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
