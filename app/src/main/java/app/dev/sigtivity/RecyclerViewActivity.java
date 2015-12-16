package app.dev.sigtivity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import app.dev.sigtivity.adapter.SigRecyclerViewAdapter;
import app.dev.sigtivity.domain.Photo;
import app.dev.sigtivity.http.HttpManager;
import app.dev.sigtivity.parser.JSONParser;

/**
 * Created by Ravi on 12/2/2015.
 */
public class RecyclerViewActivity extends Activity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private List<Photo> eventPhotos;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        mRecyclerView = (RecyclerView)findViewById(R.id.sig_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        new LoadEvenPhotos().execute("1");
    }

    private void displayEventPhotos(){
        mAdapter = new SigRecyclerViewAdapter(eventPhotos, this);
        ((SigRecyclerViewAdapter)mAdapter).setOnItemClickListner(new SigRecyclerViewAdapter.SigClickListener(){
            @Override
            public void onItemClick(int position, View v) {
                // Nothing happens yet
            }
        });

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
