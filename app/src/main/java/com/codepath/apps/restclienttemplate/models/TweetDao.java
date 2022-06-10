package com.codepath.apps.restclienttemplate.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.codepath.apps.restclienttemplate.ListUserActivity;

import java.util.List;

@Dao
public interface TweetDao {


    @Query("SELECT Tweet.body  FROM Tweet Inner JOIN User ON Tweet.userId = User.id ORDER BY Tweet.createAt DESC LIMIT 5")
    List<TweetWithUser> recentItems();


//    @Query("SELECT * FROM SampleModel WHERE id=:id")
//    SampleModel byId(Long id);
//
//    @Query("SELECT * FROM SampleModel ORDER BY ID DESC LIMIT 300")
//    List<SampleModel> recentItems();
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertModel(SampleModel... sampleModels);
}
