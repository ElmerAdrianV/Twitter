package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel

public class Tweet {
    public String body;
    public String createAt;
    public String imageURL;
    public User user;
    public Tweet(){}
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
       Tweet tweet= new Tweet();


       tweet.body=jsonObject.getString("text");
       tweet.createAt=jsonObject.getString("created_at");
       if(jsonObject.getJSONObject("entities").has("media")){
           JSONArray mediaArray=jsonObject.getJSONObject("entities").getJSONArray("media");
           if(mediaArray.length()!=0){
               JSONObject media = mediaArray.getJSONObject(0);
               tweet.imageURL=media.getString("media_url");
           }
       }

       tweet.user=User.fromJson(jsonObject.getJSONObject("user"));
       return tweet;
    }
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i=0;i<jsonArray.length();i++){
            tweets.add(Tweet.fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }
}
