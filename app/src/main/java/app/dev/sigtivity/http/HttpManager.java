package app.dev.sigtivity.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import app.dev.sigtivity.domain.EventDetail;
import app.dev.sigtivity.domain.User;

/**
 * Created by Ravi on 7/10/2015.
 */
public class HttpManager {
    private static String userLoginUrl = "http://giftandevent.com/auth/login/";

    public static String getUserPictures(int userId){
        String uri = String.format("http://giftandevent.com/photo/getuserphotos/%d", userId);
        RequestPackage requestPackage = new RequestPackage();
        requestPackage.setUri(uri);
        return getData(requestPackage);
    }
    public  static String getUserPicturesData(int userId, int eventId){
        String uri = "http://giftandevent.com/photo/getusereventphotos/" + userId + "/" + eventId;
        RequestPackage requestPackage = new RequestPackage();
        requestPackage.setUri(uri);
        return getData(requestPackage);
    }

    public static String getUserAuthData(RequestPackage requestPackage){
        requestPackage.setUri("http://giftandevent.com/auth/login/");
        return getData(requestPackage);
    }

    public static String getEventPhotosData(String eventId){
        String uri = "http://giftandevent.com/photo/eventPic/" + eventId;
        RequestPackage requestPackage = new RequestPackage();
        requestPackage.setUri(uri);
        return getData(requestPackage);
    }

    public static String getEventDetail(String userId, String eventId, String auth_token, String event_code){
        RequestPackage requestPackage = new RequestPackage();
        requestPackage.setUri(String.format("http://giftandevent.com/event/addevent/%s/%s/%s/%s", userId, eventId, auth_token, event_code));
        return getData(requestPackage);
    }

    public static String AddComent(RequestPackage requestPackage){
        requestPackage.setUri("http://giftandevent.com/comment/add/");
        requestPackage.setMethod("POST");
        return getData(requestPackage);
    }

    public static String updateCaption(RequestPackage requestPackage){
        requestPackage.setUri("http://giftandevent.com/photo/addCaption/");
        return getData(requestPackage);
    }

    public static String registerUser(RequestPackage requestPackage){
        requestPackage.setUri("http://giftandevent.com/auth/register");
        requestPackage.setMethod("POST");
        return getData(requestPackage);
    }

    public static String updateLikes(String userId, String photoId){
        RequestPackage requestPackage = new RequestPackage();
        requestPackage.setUri(String.format("http://giftandevent.com/photo/like/%s/0/%s", userId, photoId));
        return getData(requestPackage);
    }

    public static String getPictureDetail(int pictureId){
        RequestPackage requestPackage = new RequestPackage();
        requestPackage.setUri(String.format("http://giftandevent.com/photo/getphotodetail/%s", String.valueOf(pictureId)));
        return getData(requestPackage);
    }

    public static String getEventDetailByCordiates(String latittude, String longitude){
        RequestPackage requestPackage = new RequestPackage();
        requestPackage.setUri(String.format("http://giftandevent.com/event/geteventdetail/%s/%s", latittude, longitude));
        return getData(requestPackage);
    }

    public static void uploadFile(RequestPackage requestPackage) {
        String fileName = requestPackage.getParam("image_file_path");
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(fileName);

        if (!sourceFile.isFile()) {
            return;
        }
        else
        {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                String urlString = "http://giftandevent.com/sigtivityup/upload.php";
                URL url = new URL(urlString);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"user_id\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(requestPackage.getParam("user_id"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"event_id\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(requestPackage.getParam("event_id"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"photo_caption\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(requestPackage.getParam("photo_caption"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                String serverResponseMessage = conn.getResponseMessage();

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } // End else block
    }

    public static void uploadProfileImage(RequestPackage requestPackage) {
        String fileName = requestPackage.getParam("image_file_path");
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(fileName);

        if (!sourceFile.isFile()) {
            return;
        }
        else
        {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                String urlString = "http://giftandevent.com/sigtivityup/uploadprofile.php";
                URL url = new URL(urlString);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"user_id\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(requestPackage.getParam("user_id"));
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                String serverResponseMessage = conn.getResponseMessage();

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } // End else block
    }

    public  static String getData(RequestPackage requestPackage){
        BufferedReader reader = null;
        try{
            URL url = new URL(requestPackage.getUri());
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod(requestPackage.getMethod());
            if(requestPackage.getMethod().equals("POST")){
                connection.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(requestPackage.getEncodedParams());
                writer.flush();
                writer.close();
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String content =  reader.readLine();
            return  content;

        }catch (Exception ex){
            ex.printStackTrace();
            return  null;
        }finally {
            try{
                if(reader != null){
                    reader.close();
                }
            }catch (Exception ex){
                ex.printStackTrace();
                return  null;
            }
        }
    }

    public static Bitmap getProfileImage(String profileImgUrl){
        ImageLoader loader = (ImageLoader) new ImageLoader().execute(profileImgUrl);
        return loader.getImgBitmap();
    }

    private static class ImageLoader extends AsyncTask<String, Void, Bitmap> {
        Bitmap imgBitmap;

        public Bitmap getImgBitmap(){return this.imgBitmap;}

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
            imgBitmap = bitmap;
        }
    }
}
