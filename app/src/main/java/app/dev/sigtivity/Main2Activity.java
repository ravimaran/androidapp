package app.dev.sigtivity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import app.dev.sigtivity.adapter.SigRecyclerViewAdapter;
import app.dev.sigtivity.domain.Photo;
import app.dev.sigtivity.http.HttpManager;
import app.dev.sigtivity.parser.JSONParser;


public class Main2Activity extends Activity {

    private List<Photo> eventPhotos;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mRecyclerView = (RecyclerView)findViewById(R.id.sig_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
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


    private void displayEventPhotos(){
        mAdapter = new SigRecyclerViewAdapter(eventPhotos, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private class LoadEvenPhotos extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String content =  HttpManager.getEventPhotosData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String content) {
            eventPhotos = JSONParser.parsePhotos(content);
            displayEventPhotos();
        }
    }
}
