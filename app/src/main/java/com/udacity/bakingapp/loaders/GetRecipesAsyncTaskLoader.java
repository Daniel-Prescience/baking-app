package com.udacity.bakingapp.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.udacity.bakingapp.models.Ingredient;
import com.udacity.bakingapp.models.Recipe;
import com.udacity.bakingapp.models.RecipeStep;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetRecipesAsyncTaskLoader extends AsyncTaskLoader<Recipe[]> {

    private static Recipe[] mRecipes = null;

    private static final String TAG = GetRecipesAsyncTaskLoader.class.getSimpleName();

    private static final String JSON_ID_KEY = "id";
    private static final String JSON_NAME_KEY = "name";
    private static final String JSON_INGREDIENTS_KEY = "ingredients";
    private static final String JSON_INGREDIENTS_QUANTITY_KEY = "quantity";
    private static final String JSON_INGREDIENTS_MEASURE_KEY = "measure";
    private static final String JSON_INGREDIENTS_INGREDIENT_KEY = "ingredient";
    private static final String JSON_STEPS_KEY = "steps";
    private static final String JSON_STEPS_ID_KEY = "id";
    private static final String JSON_STEPS_SHORTDESCRIPTION_KEY = "shortDescription";
    private static final String JSON_STEPS_DESCRIPTION_KEY = "description";
    private static final String JSON_STEPS_VIDEOURL_KEY = "videoURL";
    private static final String JSON_STEPS_THUMBNAILURL_KEY = "thumbnailURL";
    private static final String JSON_SERVINGS_KEY = "servings";
    private static final String JSON_IMAGE_KEY = "image";

    public GetRecipesAsyncTaskLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (mRecipes != null)
            deliverResult(mRecipes);
        else
            forceLoad();
    }

    @Override
    public Recipe[] loadInBackground() {
        return getRecipes();
    }

    @Override
    public void deliverResult(Recipe[] data) {
        mRecipes = data;
        super.deliverResult(data);
    }

    private Recipe[] getRecipes() {
        try {
            String urlString = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();

            String inputString;
            while ((inputString = bufferedReader.readLine()) != null) {
                builder.append(inputString);
            }

            JSONArray recipesJson = new JSONArray(builder.toString());

            Recipe[] recipes = new Recipe[recipesJson.length()];

            //region looping through recipes
            for (int i = 0; i < recipesJson.length(); ++i) {
                JSONObject recipeJson = recipesJson.getJSONObject(i);
                JSONArray ingredientsJson = recipeJson.getJSONArray(JSON_INGREDIENTS_KEY);
                JSONArray stepsJson = recipeJson.getJSONArray(JSON_STEPS_KEY);

                Ingredient[] ingredients = new Ingredient[ingredientsJson.length()];
                for (int j = 0; j < ingredientsJson.length(); ++j) {
                    JSONObject ingredientJson = ingredientsJson.getJSONObject(j);
                    ingredients[j] = new Ingredient(
                            ingredientJson.getLong(JSON_INGREDIENTS_QUANTITY_KEY),
                            ingredientJson.getString(JSON_INGREDIENTS_MEASURE_KEY),
                            ingredientJson.getString(JSON_INGREDIENTS_INGREDIENT_KEY)
                    );
                }

                RecipeStep[] steps = new RecipeStep[stepsJson.length()];
                for (int k = 0; k < stepsJson.length(); ++k) {
                    JSONObject stepJson = stepsJson.getJSONObject(k);
                    steps[k] = new RecipeStep(
                            stepJson.getLong(JSON_STEPS_ID_KEY),
                            stepJson.getString(JSON_STEPS_SHORTDESCRIPTION_KEY),
                            stepJson.getString(JSON_STEPS_DESCRIPTION_KEY),
                            stepJson.getString(JSON_STEPS_VIDEOURL_KEY),
                            stepJson.getString(JSON_STEPS_THUMBNAILURL_KEY));
                }

                recipes[i] = new Recipe(
                        recipeJson.getLong(JSON_ID_KEY),
                        recipeJson.getString(JSON_NAME_KEY),
                        ingredients,
                        steps,
                        recipeJson.getLong(JSON_SERVINGS_KEY),
                        recipeJson.getString(JSON_IMAGE_KEY)
                );
            }
            //endregion

            return recipes;
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error", e);
            return null;
        }
    }
}