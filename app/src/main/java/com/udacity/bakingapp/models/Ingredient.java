package com.udacity.bakingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Ingredient implements Parcelable {
    private final Long quantity;
    private final String measure;
    private final String ingredient;

    public Ingredient(Long quantity, String measure, String ingredient)
    {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    @Override
    public String toString() {
        return quantity + " " + measure + " " + ingredient;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.quantity);
        dest.writeString(this.measure);
        dest.writeString(this.ingredient);
    }

    private Ingredient(Parcel in) {
        this.quantity = (Long) in.readValue(Long.class.getClassLoader());
        this.measure = in.readString();
        this.ingredient = in.readString();
    }

    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel source) {
            return new Ingredient(source);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
