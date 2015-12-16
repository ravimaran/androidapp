package app.dev.sigtivity.domain;

/**
 * Created by Ravi on 11/13/2015.
 */
public class ImageDetail {
    private int photoId;
    private String profileName;
    private String eventLocationName;
    private String photoAddedDate;
    private String profileImgUrl;
    private String imageUrl;
    private String photoCaption;
    private int commentCount;
    private int likeCount;
    private int photoCount;
    private int networkCount;
    private int eventCount;

    public int getPhotoId() {
        return photoId;
    }
    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getEventLocationName() {
        return eventLocationName;
    }

    public void setEventLocationName(String eventLocationName) {
        this.eventLocationName = eventLocationName;
    }

    public String getPhotoAddedDate() {
        return photoAddedDate;
    }

    public void setPhotoAddedDate(String photoAddedDate) {
        this.photoAddedDate = photoAddedDate;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhtoCaption() {
        return getPhotoCaption();
    }

    public void setPhtoCaption(String phtoCaption) {
        this.setPhotoCaption(phtoCaption);
    }

    public String getPhotoCaption() {
        return photoCaption;
    }

    public void setPhotoCaption(String photoCaption) {
        this.photoCaption = photoCaption;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(int photoCount) {
        this.photoCount = photoCount;
    }

    public int getNetworkCount() {
        return networkCount;
    }

    public void setNetworkCount(int networkCount) {
        this.networkCount = networkCount;
    }

    public int getEventCount() {
        return eventCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }
}