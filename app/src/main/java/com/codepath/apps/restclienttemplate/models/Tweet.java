package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity=User.class, parentColumns="id", childColumns="user_  id"))
public class Tweet {
    public String body;

    @ColumnInfo
    @PrimaryKey
    public Long tweetID;
    @ColumnInfo
    public boolean favorited;
    @ColumnInfo
    public boolean retweeted;
    @ColumnInfo
    public String createAt;
    @ColumnInfo
    public String imageURL;
    @ColumnInfo
    public Long userId;
    @Ignore
    public User user;
    @ColumnInfo
    public String relativeTimeAgo;
    @ColumnInfo
    public int favoriteCount;
    @ColumnInfo
    public int retweetCount;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public static final int NUMBER_TWEETS_REQUEST=25;
    private static final String TAG = Tweet.class.getSimpleName();

    public Tweet(){}
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
       Tweet tweet= new Tweet();


        if(jsonObject.has("full_text")) {
            tweet.body = jsonObject.getString("full_text");
        } else {
            tweet.body = jsonObject.getString("text");
        }
       tweet.createAt=jsonObject.getString("created_at");
       if(jsonObject.getJSONObject("entities").has("media")){
           JSONArray mediaArray=jsonObject.getJSONObject("entities").getJSONArray("media");
           if(mediaArray.length()!=0){
               JSONObject media = mediaArray.getJSONObject(0);
               tweet.imageURL=media.getString("media_url");
           }
       }
       tweet.user=User.fromJson(jsonObject.getJSONObject("user"));
       tweet.relativeTimeAgo= tweet.getRelativeTimeAgo(tweet.createAt);
       tweet.retweetCount= jsonObject.getInt("retweet_count");
       tweet.favoriteCount=jsonObject.getInt("favorite_count");
       tweet.tweetID=jsonObject.getLong("id");
       tweet.favorited=jsonObject.getBoolean("favorited");
       tweet.retweeted=jsonObject.getBoolean("retweeted");
       tweet.userId=tweet.user.id;
       return tweet;
    }
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i=0;i<jsonArray.length();i++){
            tweets.add(Tweet.fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }



    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (ParseException e) {
            Log.i(TAG, "getRelativeTimeAgo failed");
            e.printStackTrace();
        }

        return "";
    }
}
