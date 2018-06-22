package com.osaf.root.moviepedia.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.osaf.root.moviepedia.data.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class Movies implements Parcelable {

    //Movies Data
    private String mID;
    private String mPosterPath;
    private String mReleaseDate;
    private String mTitle;
    private String mVote;
    private String mOverview;
    private String mBackdrop;

    public Movies() {

    }

    public Movies(String id,String title, String releaseDate, String posterPath,
                  String backdrop,String vote, String overview) {
        this.mID = id;
        this.mTitle = title;
        this.mReleaseDate = releaseDate;
        this.mPosterPath = posterPath;
        this.mBackdrop = backdrop;
        this.mVote = vote;
        this.mOverview = overview;


    }
    public String  getID(){ return mID ;}


    public String getmBackdrop() { return mBackdrop; }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getVote() {
        return mVote +"/10";
    }


    @Override
    public int describeContents() {
        return 0;
    }


    protected Movies(Parcel in) {
        mID = in.readString();
        mTitle = in.readString();
        mReleaseDate = in.readString();
        mPosterPath = in.readString();
        mBackdrop = in.readString();
        mVote = in.readString();
        mOverview = in.readString();
    }



    //get favorite Movies


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mID);
        dest.writeString(mTitle);
        dest.writeString(mReleaseDate);
        dest.writeString(mPosterPath);
        dest.writeString(mBackdrop);
        dest.writeString(mVote);
        dest.writeString(mOverview);
    }
    public static final Creator<Movies> CREATOR = new Creator<Movies>() {
        public Movies createFromParcel(Parcel source) {
            return new Movies(source);
        }

        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };
}
