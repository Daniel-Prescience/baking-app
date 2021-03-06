package com.udacity.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.udacity.bakingapp.R;
import com.udacity.bakingapp.fragments.RecipeGridFragment;
import com.udacity.bakingapp.loaders.GetRecipesAsyncTaskLoader;
import com.udacity.bakingapp.models.Recipe;

public class RecipeGridActivity extends AppCompatActivity implements
        RecipeGridFragment.OnRecipeGridFragmentInteractionListener,
        LoaderManager.LoaderCallbacks {

    private static final int LOADER_ID_RECIPES = 22;
    public static Recipe[] RecipeList;

    private static FragmentManager supportFragmentManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        supportFragmentManager = getSupportFragmentManager();

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Boolean> recipeLoader = loaderManager.getLoader(LOADER_ID_RECIPES);

        if (recipeLoader == null)
            loaderManager.initLoader(LOADER_ID_RECIPES, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportLoaderManager().restartLoader(LOADER_ID_RECIPES, null, this);
    }

    private void RefreshFragmentData() {
        //region Inspired by: https://stackoverflow.com/a/31832721/5999847
        if (supportFragmentManager != null) {
            RecipeGridFragment fragment = (RecipeGridFragment) supportFragmentManager.findFragmentById(R.id.fragment_recipe_grid);

            // Notify fragment that the data set has changed and it should update is adapter.
            if (fragment != null) {
                fragment.NotifyChange();
            }
        }
        //endregion
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        return new GetRecipesAsyncTaskLoader(this);
    }

    public void onLoadFinished(@NonNull Loader loader, Object loaderData) {
        if (loaderData != null)
            RecipeList = (Recipe[]) loaderData;

        RefreshFragmentData();
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) { }

    private void launchRecipeStepListActivity(Recipe recipe) {
        Intent intent = new Intent(this, RecipeStepListActivity.class);
        intent.putExtra(RecipeStepListActivity.EXTRA_RECIPE, recipe);
        startActivity(intent);
    }

    @Override
    public void onRecipeGridFragmentInteraction(Recipe recipe) {
        launchRecipeStepListActivity(recipe);
    }
}
