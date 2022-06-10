package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity
public class User {
    @ColumnInfo
    public String name;
    @ColumnInfo
    public String screenName;
    @ColumnInfo
    public String profileImageUrl;
    @ColumnInfo
    public String description;
    @ColumnInfo
    @PrimaryKey
    public long id;


    public User(){}
    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name=jsonObject.getString("name");
        user.screenName=jsonObject.getString("screen_name");
        user.profileImageUrl=jsonObject.getString("profile_image_url_https");
        user.description=jsonObject.getString("description");
        user.id =jsonObject.getLong("id");
        return user;
    }
    public static List<User> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<User> users = new ArrayList<>();
        for(int i=0;i<jsonArray.length();i++){
            users.add(User.fromJson(jsonArray.getJSONObject(i)));
        }
        return users;
    }
}
