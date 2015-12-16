package app.dev.sigtivity.domain;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Ravi on 7/11/2015.
 */
public class User {
    private int userId;
    private int eventCount;
    private int pictureCount;
    private int networkCount;

    private String userName;
    private String password;
    private String name;
    private String profileImgUrl;
    private String userCity;
    private String userState;
    private String userOccupation;
    private String tagLine;

    private Bitmap profileBitmap;
    private Date dateAdded;
    private Date dateModified;

    // setters
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }
    public void setPictureCount(int pictureCount) {
        this.pictureCount = pictureCount;
    }
    public void setNetworkCount(int networkCount){
        this.networkCount = networkCount;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public void setUserCity(String userCity){this.userCity = userCity;}

    public void setUserState(String userState){this.userState = userState;}

    public void setUserOccupation(String userOccupation){this.userOccupation = userOccupation;}

    public void setTagLine(String tagLine){this.tagLine = tagLine;}

    public void setProfileBitmap(Bitmap profileBitmap) {
        this.profileBitmap = profileBitmap;
    }

    public void setDateAdded(Date dateAdded){this.dateAdded = dateAdded;}

    public void setDateModified(Date dateModified){this.dateModified = dateModified;}

    // getters
    public int getUserId() {
        return this.userId;
    }

    public int getEventCount() {
        return this.eventCount;
    }

    public int getPictureCount() {
        return this.pictureCount;
    }

    public int getNetworkCount(){return this.networkCount;}

    public String getUserName() {
        return this.userName;
    }

    public String getPassword() {
        return this.password;
    }

    public String getName() {
        return this.name;
    }

    public String getUserCity(){return this.userCity;}

    public String getUserState(){return this.userState;}

    public String getUserOccupation(){return this.userOccupation;}

    public String getProfileImgUrl() {
        return this.profileImgUrl;
    }

    public String getTagLine(){return  this.tagLine;}

    public Bitmap getProfileBitmap() {
        return this.profileBitmap;
    }

    public Date getDateAdded(){return this.dateAdded;}

    public Date getDateModified(){return this.dateModified;}
}
