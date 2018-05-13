package com.udacity.bakingapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Recipe implements Parcelable {
    public final Long id;
    public final String name;
    public final Ingredient[] ingredients;
    public final RecipeStep[] steps;
    public final Long servings;
    public final String image;

    public Recipe(Long id, String name, Ingredient[] ingredients, RecipeStep[] steps, Long servings, String image)
    {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeTypedArray(this.ingredients, flags);
        dest.writeTypedArray(this.steps, flags);
        dest.writeValue(this.servings);
        dest.writeString(this.image);
    }

    protected Recipe(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.ingredients = in.createTypedArray(Ingredient.CREATOR);
        this.steps = in.createTypedArray(RecipeStep.CREATOR);
        this.servings = (Long) in.readValue(Long.class.getClassLoader());
        this.image = in.readString();
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
