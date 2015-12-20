package app.dev.sigtivity.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ravi on 10/17/2015.
 */
public class ImageHelper {
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = (int)(Math.round((float) height / (float) reqHeight));
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            inSampleSize = (int)(Math.round((float) width / (float) reqWidth));
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap scaled = BitmapFactory.decodeFile(path, options);

        double newHeight = options.outHeight;
        double newWidth = options.outHeight;
        double ratio;
        if (options.outWidth > options.outHeight)
        {
            newHeight = reqHeight;
            ratio = (double)reqHeight / (double)options.outHeight;
            newWidth = options.outWidth * ratio;
        }
        else
        {
            newWidth = reqWidth;
            ratio = (double)reqWidth / (double)options.outWidth;
            newWidth = options.outHeight * ratio;
        }

        Matrix matrix = new Matrix();
        try {
            ExifInterface oldexif = new ExifInterface(path);
            int rotation = getRotation(oldexif);
            if (rotation != 0f) {
                matrix.preRotate(rotation);
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        try {
            Bitmap rescaled = Bitmap.createScaledBitmap(scaled, (int) newWidth, (int) newHeight, true);
            int x = rescaled.getWidth() / 2 - reqWidth / 2;
            int y = rescaled.getHeight() / 2 - reqHeight / 2;
            return Bitmap.createBitmap(rescaled, x, y, reqWidth, reqHeight, matrix, true);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public static Bitmap getCircleCropedImage(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        return _bmp;
        //return output;
    }

    private static int getRotation(ExifInterface exif)
    {
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
        int rotate = 0;
        switch (orientation) {
            case 3:
                rotate = 180;
                break;
            case 6:
                rotate = 90;
                break;
            case 8:
                rotate = 270;
                break;
        }

        return rotate;
    }
}
