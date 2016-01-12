package app.dev.sigtivity;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

import app.dev.sigtivity.core.GlobalConstants;


public class ActivityMain extends TabActivity {

    private String currentFragment;

    private static TabHost tabHost;
    private static ActivityMain instance;

    public static TabHost getCurrentTabHost(){
        return tabHost;
    }

    public static ActivityMain getInstance(){
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabHost = getTabHost();
        currentFragment = GlobalConstants.FRAG_SEARCH_EVENT;
        instance = this;
        // Tab for Profile
        TabHost.TabSpec profileSpec = tabHost.newTabSpec("activityprofiletab");
        profileSpec.setIndicator("", getResources().getDrawable(R.drawable.tab_activity_profile));
        Intent profileContent = new Intent(this, TabActivityProfile.class);
        profileSpec.setContent(profileContent);

        // Tab for HotSpot
        TabHost.TabSpec hotSpotSpec = tabHost.newTabSpec("activityhotspottablayout");
        hotSpotSpec.setIndicator("", getResources().getDrawable(R.drawable.tab_activity_hotspot));
        Intent hotSpotContent = new Intent(this, TabActivityHotSpot.class);
        hotSpotSpec.setContent(hotSpotContent);

        // Tab for Notification
        TabHost.TabSpec notificationSpec = tabHost.newTabSpec("activitynotificationtab");
        notificationSpec.setIndicator("", getResources().getDrawable(R.drawable.tab_activity_notify));
        Intent notificationContent = new Intent(this, TabActivityNotification.class);
        notificationSpec.setContent(notificationContent);

        tabHost.addTab(profileSpec);
        tabHost.addTab(hotSpotSpec);
        tabHost.addTab(notificationSpec);
        setTabColor(tabHost);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                showActionBar();
                switch (tabId){
                    case "activityhotspottablayout":
                        if(!currentFragment.equals(GlobalConstants.FRAG_LOAD_EVENT)) {
                            getActionBar().hide();
                        }
                        break;
                }
            }
        });
    }

    public static void setTabColor(TabHost tabhost) {
        for(int i=0;i<tabhost.getTabWidget().getChildCount();i++) {
            tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#FAFAFA")); //unselected
        }
        tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundColor(Color.parseColor("#FAFAFA")); // selected
    }

    public void buildActionBar(String actionBarTitle, String currentFragment){
        getCurrentActivity().setTitle(actionBarTitle);
        this.currentFragment = currentFragment;
        showActionBar();
    }

    public void showActionBar(){
        getActionBar().show();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate child menu
//        activityTitle = TextUtils.isEmpty(activityTitle) ?
//                String.valueOf(getCurrentActivity().getTitle()) :
//                activityTitle;
        getActionBar().setTitle(String.valueOf(getCurrentActivity().getTitle()));
        return getCurrentActivity().onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return getCurrentActivity().onOptionsItemSelected(item);
    }
}
