package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class ListUserActivity extends AppCompatActivity {
    Button btnFollowers;
    Button btnFollowings;
    TwitterClient client;
    TextView tvTitle;
    RecyclerView rvListUsers;
    ImageView ivImageProfile;
    TextView tvDescription;
    TextView tvName;
    TextView tvScreenName;
    User user;
    List<User> users;
    Followers_FollowingAdapter adapter;
    String  TAG = ListUserActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);
        client = TwitterApp.getRestClient(this);
        user = (User) Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));
        ///Find the recycler view
        rvListUsers = findViewById(R.id.rvListUsers);
        //init the list of tweets and adapter
        users = new ArrayList<>();
        adapter = new Followers_FollowingAdapter(this, users);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvListUsers.setLayoutManager(linearLayoutManager);
        rvListUsers.setAdapter(adapter);
        btnFollowers=findViewById(R.id.btnFollowers);
        btnFollowings=findViewById(R.id.btnFollowing);
        tvTitle=findViewById(R.id.tvTitle);
        tvDescription=findViewById(R.id.tvDescription);
        tvName=findViewById(R.id.tvName);
        tvScreenName=findViewById(R.id.tvScreenName);
        ivImageProfile = findViewById(R.id.ivImageProfile);

        //Log.d(TAG, "bind: "+tweet.body);
        int radiusIP = 100; // corner radius, higher value = more rounded
        tvDescription.setText(user.description);
        tvName.setText(user.name);
        tvScreenName.setText("@"+user.screenName);
        Glide.with(this).load(user.profileImageUrl)
                .apply(new RequestOptions()
                        .centerCrop() // scale image to fill the entire ImageView
                        .transform(new RoundedCorners(radiusIP))
                )
                .into(ivImageProfile);
        tvTitle.setText("Followers of "+user.name);
        populateFollowers();
        btnFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvTitle.setText("Followers of "+user.name);
                populateFollowers();
            }
        });

        btnFollowings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvTitle.setText("Following of "+user.name);
                populateFollowing();
            }
        });


    }
    private void populateFollowers() {
        client.getFolllowers(user.user_id,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG,"OnSuccess"+json.toString() );


                try{
                    JSONArray jsonArray= json.jsonObject.getJSONArray("users");
                    adapter.clear();
                    users.addAll(User.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                    rvListUsers.smoothScrollToPosition(0);
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
    private void populateFollowing() {
        client.getFollowing(user.user_id,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG,"OnSuccess"+json.toString() );

                try{
                    JSONArray jsonArray= json.jsonObject.getJSONArray("users");
                    adapter.clear();
                    users.addAll(User.fromJsonArray(jsonArray));
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