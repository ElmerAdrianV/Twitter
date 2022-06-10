package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    TwitterClient client;
    public static final String TAG = "TimelineActivity.java";
    private final int REQUEST_CODE = 20;
    private int currentOffset=25;//Count number of tweets in the timeline
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;

    private SwipeRefreshLayout swipeContainer;
    FloatingActionButton fabCompose;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        fabCompose = findViewById(R.id.fabCompose);
        client = TwitterApp.getRestClient(this);
        ///Find the recycler view
        rvTweets = findViewById(R.id.rvTweets);
        //init the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(adapter);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);



        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //recycler view setup: layout manager and the adapter
        populateHomeTimeline();

        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Compose icon has been selected
                Toast.makeText(TimelineActivity.this, "Compose!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
            }


        });
    }
    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {




        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        client.getHomeTimeLine(new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {

                Log.i(TAG,"OnSuccess"+json.toString() );

                JSONArray jsonArray= json.jsonArray;
                try{
                    //  --> Deserialize and construct new model objects from the API response
                    //  --> Append the new data objects to the existing set of items inside the array of items
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));

                    //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`

                    adapter.notifyItemRangeInserted(currentOffset,25);
                    currentOffset+=25;
                }
                catch(JSONException e){
                    Log.e(TAG,"JsonException"+e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG,"OnFailure", throwable );
            }
        },currentOffset);

    }
    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.getHomeTimeLine(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Remember to CLEAR OUT old items before appending in the new ones
                JSONArray jsonArray= json.jsonArray;
                adapter.clear();
                // ...the data has come back, add new items to your adapter...
                try {
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
                rvTweets.smoothScrollToPosition(0);
                Log.d(TAG, "onSuccess: Estoy funcionando");


            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("DEBUG", "Fetch timeline error: " + throwable);

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
        switch (item.getItemId()){
            case R.id.logout:
                // forget who's logged in
                TwitterApp.getRestClient(TimelineActivity.this).clearAccessToken();

                // navigate backwards to Login screen
                Intent i = new Intent(TimelineActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK){
            //Get data from the intent (tweet)
            Tweet tweet =(Tweet) Parcels.unwrap(data.getParcelableExtra("tweet"));
            // Update the RV with the tweet
            //Modify data source of tweets
            tweets.add(0,tweet);
            //Update the adapter
           adapter.notifyItemInserted(0);
           //To return to position 0
           rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeLine(new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG,"OnSuccess"+json.toString() );

                JSONArray jsonArray= json.jsonArray;
                try{

                    //Log.d(TAG,Tweet.fromJsonArray(jsonArray).toString());
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
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