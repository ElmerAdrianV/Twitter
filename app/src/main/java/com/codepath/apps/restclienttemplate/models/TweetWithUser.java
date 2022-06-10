package com.codepath.apps.restclienttemplate.models;

import androidx.room.Embedded;

public class TweetWithUser {
    @Embedded
    User user;
    @Embedded(prefix="tweet_")
    Tweet tweet;

}
