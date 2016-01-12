package app.dev.sigtivity.parser;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import app.dev.sigtivity.domain.Comment;
import app.dev.sigtivity.domain.EventDetail;
import app.dev.sigtivity.domain.ImageDetail;
import app.dev.sigtivity.domain.LikeLookup;
import app.dev.sigtivity.domain.Photo;
import app.dev.sigtivity.domain.RegisterValidation;
import app.dev.sigtivity.domain.User;
import app.dev.sigtivity.domain.UserAuthentication;
import app.dev.sigtivity.http.HttpManager;
import app.dev.sigtivity.utils.CommonHelper;

/**
 * Created by Ravi on 7/10/2015.
 */
public class JSONParser {

    public static List<Photo> parsePhotos(String jsonString){
        List<Photo> photos = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(jsonString);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                Photo pic = new Photo();
                pic.setPictureId(obj.getInt("photo_id"));
                if(obj.has("user_id")){
                    pic.setPictureProfileId(obj.getInt("user_id"));
                }

                pic.setUserName(obj.getString("name"));
                pic.setImageUrl(obj.getString("fullsize"));
                pic.setPhotoCaption(obj.getString("photo_caption"));
                pic.setPhotoName(obj.getString("photo_name"));
                // set date value
                String strDate = obj.getString("date_added");
                pic.setDateTaken(CommonHelper.parseDate(strDate));
                if(obj.has("comments")) {
                    pic.setComments(obj.getInt("comments"));
                }

                if(obj.has("likes")) {
                    pic.setLikes(obj.getInt("likes"));
                }

                if(obj.has("profileimage")) {
                    pic.setProfileImgUrl(obj.getString("profileimage"));
                }

                if(obj.has("thumbnail")){
                    pic.setThumbnail(obj.getString("thumbnail"));
                }

                photos.add(pic);
            }
        }catch(JSONException ex){
            ex.printStackTrace();
        }

        return photos;
    }

    public static UserAuthentication parseUserAuthentication(String jsonString){
        UserAuthentication userAuth = new UserAuthentication();
        try{
            JSONObject jsonObject = new JSONObject(jsonString);
            userAuth.setLoggedin(jsonObject.getBoolean("logged"));
            if(userAuth.isLoggedin()){
                JSONObject messageObject = new JSONObject(jsonObject.getString("message"));
                userAuth.setUserid(messageObject.getInt("user_id"));
                userAuth.setAuthToken(messageObject.getString("auth_token"));
            }else{
                userAuth.setMessage(jsonObject.getString("message"));
            }

        }catch (JSONException ex){
            ex.printStackTrace();
        }

        return userAuth;
    }

    public static EventDetail parseEventDetail(String jsonString){
        EventDetail eventDetail = new EventDetail();
        try{
            JSONArray jsonArray = new JSONArray(jsonString);
            JSONObject obj = jsonArray.getJSONObject(0);
            eventDetail.setAddress(obj.getString("address"));
            eventDetail.setCity(obj.getString("city"));
            eventDetail.setCountry(obj.getString("country"));
            eventDetail.setDescription(obj.getString("description"));
            eventDetail.setEventDate(CommonHelper.parseDate(obj.getString("event_date")));
            eventDetail.setEventDay(obj.getString("event_day"));
            eventDetail.setEventId(obj.getInt("event_id"));
            eventDetail.setEventMonth(obj.getString("event_month"));
            eventDetail.setEventName(obj.getString("event_name"));
            eventDetail.setEventWeekDay(obj.getString("event_weekday"));
            eventDetail.setEventYear(obj.getString("event_year"));
            eventDetail.setLatitude(obj.getString("latitude"));
            eventDetail.setLocationName(obj.getString("location_name"));
            eventDetail.setLongitude(obj.getString("longitude"));
            eventDetail.setEventCode(obj.getString("event_code"));
            eventDetail.setParticipants(obj.getInt("participants"));
            eventDetail.setTotalPhotos(obj.getInt("total_photos"));

        }catch (JSONException ex){
            ex.printStackTrace();
            eventDetail = null;
        }

        return eventDetail;
    }

    public static List<Comment> parseComments(String jsonString){
        List<Comment> comments = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(jsonString);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                Comment comment = new Comment();
                //comment.setPhotoId(obj.getInt("photo_id"));
                comment.setUserName(obj.getString("name"));
                comment.setUserId(obj.getInt("user_id"));
                comment.setCommentId(obj.getInt("comment_id"));
                comment.setUserComment(obj.getString("comment"));
                // set date value
                String dateAdded = obj.getString("date_added");
                comment.setDateAdded(CommonHelper.parseDate(dateAdded));
                String dateModified = obj.getString("date_modified");
                comment.setDateModified(CommonHelper.parseDate(dateModified));
                comment.setUserProfileImage(obj.getString("profileimage"));
                comments.add(comment);
            }
        }catch(JSONException ex){
            ex.printStackTrace();
        }

        return comments;
    }

    public static RegisterValidation parseRegisterValidation(String jsonString){
        RegisterValidation validation = new RegisterValidation();
        try{
            JSONObject obj = new JSONObject(jsonString);
            validation.setEmailExists(obj.getBoolean("emailExists"));
            validation.setUsernameExists(obj.getBoolean("usernameExists"));

        }catch (JSONException ex){
            ex.printStackTrace();
        }

        return validation;
    }

    public static User parseUser(String jsonString){
        User user = new User();
        try{
            JSONObject obj = new JSONObject(jsonString);
            user.setUserId(obj.getInt("user_id"));
            user.setEventCount(obj.getInt("event_count"));
            user.setPictureCount(obj.getInt("photo_count"));
            user.setNetworkCount(obj.getInt("network_count"));
            user.setUserName(obj.getString("username"));
            user.setPassword(obj.getString("password"));
            user.setName(obj.getString("name"));
            user.setProfileImgUrl(obj.getString("profileimage"));
            user.setUserCity(obj.getString("user_city"));
            user.setUserState(obj.getString("user_state"));
            user.setUserOccupation(obj.getString("user_occupation"));
            user.setTagLine(obj.getString("tag_line"));
            String strDateAdded = obj.getString("date_added");
            user.setDateAdded(CommonHelper.parseDate(strDateAdded));
            String strDateModified = obj.getString("date_modified");
            user.setDateModified(CommonHelper.parseDate(strDateModified));
            user.setProfileBitmap(HttpManager.getProfileImage(user.getProfileImgUrl()));

        }catch (JSONException ex){
            ex.printStackTrace();
        }

        return user;
    }

    public static ImageDetail parseImageDetail(String jsonString){
        ImageDetail detail = new ImageDetail();
        try{
            JSONArray jsonArray = new JSONArray(jsonString);
            JSONObject obj = jsonArray.getJSONObject(0);
            detail.setEventLocationName(obj.getString("location_name"));
            detail.setProfileImgUrl(obj.getString("profileimage"));
            detail.setPhotoAddedDate( obj.getString("date_added"));
            detail.setImageUrl(obj.getString("fullsize"));
            detail.setPhtoCaption(obj.getString("photo_caption"));
            detail.setProfileName(obj.getString("name"));
            detail.setLikeCount(obj.getInt("likes"));
            detail.setCommentCount(obj.getInt("comments"));
//            detail.setPhotoCount(obj.getInt("photo_count"));
//            detail.setEventCount(obj.getInt("event_count"));
//            detail.setNetworkCount(obj.getInt("network_count"));
        }catch (Exception ex){
            ex.printStackTrace();
            detail = null;
        }

        return detail;
    }

    public static LikeLookup parseLikeLookup(String jsonString){
        LikeLookup lookup = new LikeLookup();
        try{
            JSONObject obj = new JSONObject(jsonString);
            lookup.setLiked(obj.getBoolean("liked"));
            lookup.setLikes(obj.getInt("likes"));
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return lookup;
    }

}
