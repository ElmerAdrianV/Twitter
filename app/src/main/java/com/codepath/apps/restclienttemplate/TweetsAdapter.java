package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;
import java.util.regex.Pattern;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;



    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }
    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }
    //For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.item_tweet,parent,false);
       return new ViewHolder(view);
    }
    //Bind value based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Get the data at position
        Tweet tweet = tweets.get(position);

        //Bind the tweet with view holder
        holder.bind(tweet);
    }


    @Override
    public int getItemCount() {
        return tweets.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivImage;
        TextView tvBody;
        TextView tvName;
        TextView tvRelativeTimeAgo;
        TextView tvScreenName;
        TextView tvFavCount;
        TextView tvRetweetCount;
        ImageButton btnLike;
        ImageButton btnRetweet;
        ImageButton btnImageProfile;



        public ViewHolder(@NonNull View itemView){
            super(itemView);

            ivImage=itemView.findViewById(R.id.ivImage);
            tvBody=itemView.findViewById(R.id.tvBody);
            tvName=itemView.findViewById(R.id.tvName);
            tvScreenName=itemView.findViewById(R.id.tvScreenName);
            tvRelativeTimeAgo=itemView.findViewById(R.id.tvRelativeTimeAgo);
            tvFavCount=itemView.findViewById(R.id.tvFavCount);
            tvRetweetCount=itemView.findViewById(R.id.tvRetweetCount);
            btnLike=itemView.findViewById(R.id.btnLike);
            btnRetweet=itemView.findViewById(R.id.btnRetweet);
            btnImageProfile = itemView.findViewById(R.id.ivImageProfile);
            itemView.setOnClickListener(this);
        }


        public void bind(Tweet tweet) {
            //Log.d(TAG, "bind: "+tweet.body);
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
                                    Intent intent = new Intent(context, ListUserActivity.class);
                                    intent.putExtra("User", Parcels.wrap( tweet.user) );
                                    context.startActivity(intent);
                                }
                            }).into(tvScreenName);


            Glide.with(context).load(tweet.user.profileImageUrl)
                    .apply(new RequestOptions()
                            .centerCrop() // scale image to fill the entire ImageView
                            .transform(new RoundedCorners(radiusIP))
                    )
                    .into(btnImageProfile);
            if(tweet.imageURL!=null){

                Glide.with(context).load(tweet.imageURL)
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
            tvRelativeTimeAgo.setText(tweet.relativeTimeAgo);

            tvFavCount.setText(tweet.favoriteCount+"");
            tvRetweetCount.setText(tweet.retweetCount+"");
            TwitterClient client = new TwitterClient(context);
            if(tweet.favorited)
                btnLike.setImageResource(R.drawable.ic_vector_heart);


            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tweet.favorited=!tweet.favorited;

                    if(tweet.favorited) {
                        client.likeTweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                tweet.favoriteCount++;
                                tvFavCount.setText(tweet.favoriteCount +"");
                                btnLike.setImageResource(R.drawable.ic_vector_heart);
                                //Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });
                    }
                    else {

                        client.unLikeTweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                tweet.favoriteCount--;
                                tvFavCount.setText(tweet.favoriteCount+"");
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
                        client.retweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                tweet.retweetCount++;
                                tvRetweetCount.setText(tweet.retweetCount+"");
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

                        client.unretweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                tweet.retweetCount--;
                                tvRetweetCount.setText(tweet.retweetCount+"");
                                btnRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                                //Toast.makeText(context, "Unretweet", Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(context, ListUserActivity.class);
                    intent.putExtra("User", Parcels.wrap( tweet.user) );
                    context.startActivity(intent);
                }
            });


        }
        @Override
        public void onClick(View v) {
            //gets item position
            int position=getAdapterPosition();
            Toast.makeText(context,"HOlaaa",Toast.LENGTH_LONG).show();
            if(position!=RecyclerView.NO_POSITION){
                Tweet movie = tweets.get(position);
                //create intent for the new activity
                Intent intent = new Intent(context, TweetDetails.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(movie));
                //show the activity
                context.startActivity(intent);
                Log.d("Onclick///", "onClick:Entree ");
            }
        }


    }


}
