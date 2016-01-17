package app.dev.sigtivity;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.util.List;

import app.dev.sigtivity.core.GlobalConstants;
import app.dev.sigtivity.core.PreferenceManager;
import app.dev.sigtivity.domain.EventDetail;
import app.dev.sigtivity.http.HttpManager;
import app.dev.sigtivity.parser.JSONParser;


public class ActivityUserEvents extends Activity implements FragmentLoadHotSpotFeed.OnHotSpotFragmentInteractionListener {

    private int userId;
    private int userEventId;
    private EventDetail eventDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_events);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        userId = Integer.parseInt(PreferenceManager.getUserId(this));
        Bundle bundle = getIntent().getExtras();
        userEventId = bundle.getInt(GlobalConstants.KEY_USER_EVENT_ID);
        new LoadUserEvents().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_user_events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadEventPictures(){
        getActionBar().setTitle(eventDetail.getEventName());
        FragmentLoadHotSpotFeed fragment = new FragmentLoadHotSpotFeed();
        fragment.setCurrentActivityAndEvent(this, eventDetail, String.valueOf(userId), false);
        getFragmentManager().beginTransaction()
                .add(R.id.frameLayoutEventPics, fragment)
                .commit();
    }

    @Override
    public void onHotSpotFragmentInteraction() {

    }

    private class LoadUserEvents extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            return HttpManager.getEventDetail(userEventId);
        }

        @Override
        protected void onPostExecute(String jsonString) {
            List<EventDetail> eventDetails = JSONParser.getUserEvents(jsonString);
            eventDetail = eventDetails.get(0);
            loadEventPictures();
        }
    }
}
