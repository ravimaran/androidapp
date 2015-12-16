package app.dev.sigtivity;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;


public class ActivityPhotoCaption extends Activity {

    private EditText caption;
    private int photId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_addcaption);
        caption = (EditText) findViewById(R.id.txtCaption);
        setEditCaptionTextFocus(false);
        initializeEvents();
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_activity_photo_caption, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public void saveCaption(View view){}

    private void setEditCaptionTextFocus(boolean editCaptionTextFocus) {
        caption.setFocusable(editCaptionTextFocus);
        caption.setFocusableInTouchMode(editCaptionTextFocus);
    }

    private void initializeEvents() {
        caption.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    setEditCaptionTextFocus(true);
                }

                return false;
            }
        });
    }
}
