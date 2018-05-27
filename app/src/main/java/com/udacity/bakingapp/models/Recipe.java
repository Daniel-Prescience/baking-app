package com.udacity.bakingapp.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class Recipe implements Parcelable {
    private final Long id;
    public final String name;
    private final Ingredient[] ingredients;
    public final RecipeStep[] steps;
    private final Long servings;
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

    public String getIngredientsAsString() {
        List<String> ingredientStringList = new ArrayList<>();
        for (Ingredient ingredient: ingredients) {
            ingredientStringList.add(ingredient.toString());
        }
        return TextUtils.join("\n", ingredientStringList);
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

    private Recipe(Parcel in) {
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
