package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.regex.Pattern;

import okhttp3.Headers;

public class TweetDetails extends AppCompatActivity {
    Tweet tweet;
    ImageView ivImage;
    TextView tvBody;
    TextView tvName;
    TextView tvScreenName;
    ImageButton btnLike;
    ImageButton btnRetweet;
    ImageButton btnImageProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        ivImage=findViewById(R.id.ivImage);
        tvBody=findViewById(R.id.tvBody);
        tvName=findViewById(R.id.tvName);
        tvScreenName=findViewById(R.id.tvScreenName);
        btnLike=findViewById(R.id.btnLike);
        btnRetweet=findViewById(R.id.btnRetweet);
        btnImageProfile = findViewById(R.id.ivImageProfile);
        TwitterClient client=new TwitterClient(this);

        int radiusIP = 100; // corner radius, higher value = more rounded
        int radiusM = 80;
        tvBody.setText(tweet.body);
        new PatternEditableBuilder().addPattern(Pattern.compile("\\#(\\w+)"), Color.CYAN,
                new PatternEditableBuilder.SpannableClickedListener() {
                    @Override
                    public void onSpanClicked(String text) {
                        //Toast.makeText(context, "Clicked hashtag: " + text,Toast.LENGTH_SHORT).show();
                    }
                }).into(tvBody);

        tvName.setText(tweet.user.name);
        tvScreenName.setText("@"+tweet.user.screenName);
        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), Color.rgb(188, 186, 186),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Intent intent = new Intent(TweetDetails.this,ListUserActivity.class);
                                intent.putExtra("User", Parcels.wrap( tweet.user) );
                                startActivity(intent);
                            }
                        }).into(tvScreenName);


        Glide.with(this).load(tweet.user.profileImageUrl)
                .apply(new RequestOptions()
                        .centerCrop() // scale image to fill the entire ImageView
                        .transform(new RoundedCorners(radiusIP))
                )
                .into(btnImageProfile);
        if(tweet.imageURL!=null){

            Glide.with(this).load(tweet.imageURL)
                    .apply(new RequestOptions()
                            .centerCrop() // scale image to fill the entire ImageView
                            .transform(new RoundedCorners(radiusM))
                    )
                    .into(ivImage);
            ivImage.setVisibility(View.VISIBLE);
        }
        else{
            ivImage.setVisibility(View.GONE);
        }

        if(tweet.favorited)
            btnLike.setImageResource(R.drawable.ic_vector_heart);


        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweet.favorited=!tweet.favorited;

                if(tweet.favorited) {
                    client.likeTweet(tweet.tweetID, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            tweet.favoriteCount++;

                            btnLike.setImageResource(R.drawable.ic_vector_heart);
                            //Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        }
                    });
                }
                else {

                    client.unLikeTweet(tweet.tweetID, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            tweet.favoriteCount--;

                            btnLike.setImageResource(R.drawable.ic_vector_heart_stroke);
                            //Toast.makeText(context, "Unliked", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        }
                    });
                }

            }
        });
        if(tweet.retweeted)
            btnRetweet.setImageResource(R.drawable.ic_vector_retweet);

        btnRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweet.retweeted=!tweet.retweeted;

                if(tweet.retweeted) {
                    client.retweet(tweet.tweetID, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            tweet.retweetCount++;

                            btnRetweet.setImageResource(R.drawable.ic_vector_retweet);
                            //Toast.makeText(context, "Retweet", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            //Toast.makeText(context, "Error1", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {

                    client.unretweet(tweet.tweetID, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            tweet.retweetCount--;

                            btnRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);

                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            //Toast.makeText(context, "Error2", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        btnImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TweetDetails.this, ListUserActivity.class);
                intent.putExtra("User", Parcels.wrap( tweet.user) );
                startActivity(intent);
            }
        });

    }
}