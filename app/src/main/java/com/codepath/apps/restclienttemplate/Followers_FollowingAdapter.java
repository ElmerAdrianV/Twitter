package com.codepath.apps.restclienttemplate;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;


import java.util.List;


import okhttp3.Headers;

public class Followers_FollowingAdapter extends RecyclerView.Adapter<Followers_FollowingAdapter.ViewHolder> {
    Context context;
    List<User> users;
    public Followers_FollowingAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }
    // Clean all elements of the recycler
    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<User> list) {
        users.addAll(list);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public Followers_FollowingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Followers_FollowingAdapter.ViewHolder holder, int position) {
        //Get the data at position
        User user = users.get(position);
        //Bind the tweet with view holder
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivImageProfile;
        TextView tvDescription;
        TextView tvName;
        TextView tvScreenName;

        TwitterClient client = new TwitterClient(context);
        public ViewHolder(@NonNull View itemView){
            super(itemView);

            tvDescription=itemView.findViewById(R.id.tvDescription);
            tvName=itemView.findViewById(R.id.tvName);
            tvScreenName=itemView.findViewById(R.id.tvScreenName);
            ivImageProfile = itemView.findViewById(R.id.ivImageProfile);
        }

        public void bind(User user) {
            //Log.d(TAG, "bind: "+tweet.body);
            int radiusIP = 100; // corner radius, higher value = more rounded
            tvDescription.setText(user.description);
            tvName.setText(user.name);
            tvScreenName.setText("@"+user.screenName);
            Glide.with(context).load(user.profileImageUrl)
                    .apply(new RequestOptions()
                            .centerCrop() // scale image to fill the entire ImageView
                            .transform(new RoundedCorners(radiusIP))
                    )
                    .into(ivImageProfile);
        }
    }

}
