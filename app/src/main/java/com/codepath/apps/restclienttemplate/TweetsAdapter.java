package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

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
        TextView tvName;
        TextView tvRelativeTimeAgo;
        TextView tvScreenName;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            ivProfileImage=itemView.findViewById(R.id.ivProfileImage);
            ivImage=itemView.findViewById(R.id.ivImage);
            tvBody=itemView.findViewById(R.id.tvBody);
            tvName=itemView.findViewById(R.id.tvName);
            tvScreenName=itemView.findViewById(R.id.tvScreenName);
            tvRelativeTimeAgo=itemView.findViewById(R.id.tvRelativeTimeAgo);
        }

        public void bind(Tweet tweet) {
            //Log.d(TAG, "bind: "+tweet.body);
            int radiusIP = 100; // corner radius, higher value = more rounded
            int radiusM = 80;
            tvBody.setText(tweet.body);
            tvName.setText(tweet.user.name);
            tvScreenName.setText("@"+tweet.user.screenName);

            Glide.with(context).load(tweet.user.profileImageUrl)
                    .apply(new RequestOptions()
                            .centerCrop() // scale image to fill the entire ImageView
                            .transform(new RoundedCorners(radiusIP))
                    )
                    .into(ivProfileImage);
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

        }
    }
}
