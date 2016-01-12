package app.dev.sigtivity;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import app.dev.sigtivity.core.PreferenceManager;
import app.dev.sigtivity.domain.EventDetail;
import app.dev.sigtivity.helper.ImageHelper;
import app.dev.sigtivity.http.HttpManager;
import app.dev.sigtivity.parser.JSONParser;


public class TabActivityHotSpot extends Activity implements
        FragmentValidateEventCode.OnEventCodeFragmentInteractionListener,
        FragmentLoadHotSpotFeed.OnHotSpotFragmentInteractionListener,
        FragmentPhotoCaption.OnPhotoCaptionFragmentInteractionListener{

    private SIGLocationListener sigLocationListner;
    private LocationManager sigLocationManager;
    private String provider;
    private Criteria criteria;

    private double latitude;
    private double longitude;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private Uri fileUri;
    private EventDetail eventDetail;
    private Bitmap scaledBitmap;

    private FragmentLoadHotSpotFeed fragmentLoadHotSpotFeed;

    public void setFileUri(Uri fileUri){
        this.fileUri = fileUri;
    }

    @Override
    public void onEventCodeFragmentInteraction(EventDetail eventDetail) {
        this.eventDetail = eventDetail;
        loadHotSpotEventFragment();
    }

    @Override
    public void onHotSpotFragmentInteraction() {
    }

    @Override
    public void onPhotoCaptionFragmentInteraction(String caption) {
        fragmentLoadHotSpotFeed.updateTakenPhoto(caption, scaledBitmap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_spot_layout);
        FragmentSearchEvent fragmentSearchEvent = new FragmentSearchEvent();
        getFragmentManager().beginTransaction()
                .add(R.id.hotspotFrameLayout, fragmentSearchEvent)
                .commit();
        // set location
        initializeLocationService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab_activity_hot_spot_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                scaledBitmap = ImageHelper.decodeSampledBitmapFromFile(fileUri.getPath(), 600, 600);
                try {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    DialogFragment photoCaption = FragmentPhotoCaption.newInstance(scaledBitmap);
                    ft.add(photoCaption, null);
                    ft.commitAllowingStateLoss();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private void initializeLocationService(){
        sigLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        provider = sigLocationManager.getBestProvider(criteria, false);
        Location location = sigLocationManager.getLastKnownLocation(provider);
        //sigLocationListner = new SIGLocationListener();
        if(location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            //sigLocationListner.onLocationChanged(location);
            new EventFinder().execute();
        }else{
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        sigLocationManager.requestLocationUpdates(provider, 1, 200, new SIGLocationListener());
    }

    private void loadEventCodeFragment(){
        FragmentValidateEventCode fragmentValidateEventCode = new FragmentValidateEventCode();
        fragmentValidateEventCode.setCordinates(String.valueOf(latitude), String.valueOf(longitude));
        getFragmentManager().beginTransaction()
                .replace(R.id.hotspotFrameLayout, fragmentValidateEventCode)
                .commit();
    }

    private void loadHotSpotEventFragment(){
        String userId = PreferenceManager.getUserId(this);
        fragmentLoadHotSpotFeed = new FragmentLoadHotSpotFeed();
        fragmentLoadHotSpotFeed.setCurrentActivityAndEvent(this, eventDetail, userId);
        getFragmentManager().beginTransaction()
                .replace(R.id.hotspotFrameLayout, fragmentLoadHotSpotFeed)
                .commit();
    }

    private void displayEmptyEvent(){
        DialogFragment dialogFragment = new DialogFragmentNoEvent();
        dialogFragment.show(getFragmentManager(), "emptyevent");
    }

    private void processLocationChange(){
        if(eventDetail != null){
            // event already found
            double startLatitude = Double.parseDouble(eventDetail.getLatitude());
            double startLongitude = Double.parseDouble(eventDetail.getLongitude());
//            double endLatitude = Double.parseDouble(latitude);
//            double endLongitude = Double.parseDouble(longitude);
//            float[] result = new float[1];
//            Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, result);
//            float distance = result[0];
//            if(distance > 0.01){
//                DialogFragment fragment = new DialogFragmentOutOfHotSpot();
//                fragment.show(getFragmentManager(), "outofhotspot");
//            }
        }
    }
    //Internal class
    private class SIGLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            // Initialize the location fields
//            latitude = String.valueOf(location.getLatitude());
//            longitude = String.valueOf(location.getLongitude());
//            processLocationChange();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }

    private class EventFinder extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... params) {
            return HttpManager.getEventDetailByCordiates(String.valueOf(latitude), String.valueOf(longitude));
        }

        @Override
        protected void onPostExecute(String jsonString) {
            eventDetail = JSONParser.parseEventDetail(jsonString);
            if(eventDetail != null) {
                loadEventCodeFragment();
                
                displayEmptyEvent();
            }
        }
    }
}
