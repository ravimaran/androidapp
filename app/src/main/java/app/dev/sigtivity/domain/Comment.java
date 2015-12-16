package app.dev.sigtivity.domain;

import java.util.Date;

/**
 * Created by Ravi on 10/4/2015.
 */
public class Comment {
    int commentId;
    int photoId;
    int userId;
    String userComment;
    String userName;
    private String userProfileImage;
    Date dateAdded;
    Date dateModified;

    public void setCommentId(int commentId){
        this.commentId = commentId;
    }

    public void setPhotoId(int photoId){
        this.photoId = photoId;
    }

    public  void setUserId(int userId){
        this.userId = userId;
    }

    public void setUserComment(String userComment){
        this.userComment = userComment;
    }

    public void setUserName(String userName){this.userName = userName; }

    public void setDateAdded(Date dateAdded){
        this.dateAdded = dateAdded;
    }

    public void setDateModified(Date dateModified){
        this.dateModified = dateModified;
    }

    public int getCommentId(){return  this.photoId;}
    public int getPhotoId(){return this.photoId;}
    public int getUserId(){return this.userId;}
    public String getUserComment(){return  this.userComment;}
    public String getUserName(){return this.userName; }
    public Date getDateAdded(){return this.dateAdded;}
    public Date getDateModified(){return this.dateModified;}

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }
}
