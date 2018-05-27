package com.udacity.bakingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class RecipeStep implements Parcelable {
    private final Long id;
    public final String shortDescription;
    public final String description;
    public final String videoURL;
    public final String thumbnailURL;
    public final String fallbackImageURL;

    public RecipeStep(Long id, String shortDescription, String description, String videoURL, String thumbnailURL, String fallbackImageURL)
    {
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
        this.fallbackImageURL = fallbackImageURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.shortDescription);
        dest.writeString(this.description);
        dest.writeString(this.videoURL);
        dest.writeString(this.thumbnailURL);
        dest.writeString(this.fallbackImageURL);
    }

    private RecipeStep(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.shortDescription = in.readString();
        this.description = in.readString();
        this.videoURL = in.readString();
        this.thumbnailURL = in.readString();
        this.fallbackImageURL = in.readString();
    }

    public static final Parcelable.Creator<RecipeStep> CREATOR = new Parcelable.Creator<RecipeStep>() {
        @Override
        public RecipeStep createFromParcel(Parcel source) {
            return new RecipeStep(source);
        }

        @Override
        public RecipeStep[] newArray(int size) {
            return new RecipeStep[size];
        }
    };
}
