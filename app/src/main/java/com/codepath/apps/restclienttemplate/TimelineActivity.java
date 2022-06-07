package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    TwitterClient client;
    String TAG= "TimelineActivity.java";
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    Button btnLogout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client =TwitterApp.getRestClient(this);
        ///Find the recycler view
         rvTweets = findViewById(R.id.rvTweets);
        //init the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        btnLogout=findViewById(R.id.btnLogout);

        //recycler view setup: layout manager and the adapter
        populateHomeTimeline();
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onLogoutButton(v);
            }

            void onLogoutButton(View view) {
                // forget who's logged in
                TwitterApp.getRestClient(TimelineActivity.this).clearAccessToken();

                // navigate backwards to Login screen
                Intent i = new Intent(TimelineActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
                startActivity(i);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.compose){
            //Compose icon has been selected
            //Toast.makeText(this, "Compose!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ComposeActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeLine(new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG,"OnSuccess"+json.toString() );
                JSONArray jsonArray= json.jsonArray;
                try{

                    Log.d(TAG,Tweet.fromJsonArray(jsonArray).toString());
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
//                    for (Tweet tweet:tweets){
//                        Log.d(TAG,tweet.body);
//                    }
                    adapter.notifyDataSetChanged();
                }
                catch(JSONException e){
                    Log.e(TAG,"JsonException"+e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG,"OnFailure", throwable );
            }
        });
    }




}