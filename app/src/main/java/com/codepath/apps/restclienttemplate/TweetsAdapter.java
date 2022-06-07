package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;
import java.util.ListResourceBundle;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;
    String TAG= TweetsAdapter.class.getSimpleName();

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

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivProfileImage;
        ImageView ivImage;
        TextView tvBody;
        TextView tvScreenName;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            ivProfileImage=itemView.findViewById(R.id.ivProfileImage);
            ivImage=itemView.findViewById(R.id.ivImage);
            tvBody=itemView.findViewById(R.id.tvBody);
            tvScreenName=itemView.findViewById(R.id.tvScreenName);
        }

        public void bind(Tweet tweet) {
            //Log.d(TAG, "bind: "+tweet.body);
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
            if(tweet.imageURL!=null){
                Glide.with(context).load(tweet.imageURL).into(ivImage);
                ivImage.setVisibility(View.VISIBLE);
            }
            else{
                ivImage.setVisibility(View.GONE);
            }

        }
    }
}
