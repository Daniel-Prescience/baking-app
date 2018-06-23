package com.udacity.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.udacity.bakingapp.R;
import com.udacity.bakingapp.adapters.RecipeStepListRecyclerViewAdapter;
import com.udacity.bakingapp.fragments.RecipeStepDetailFragment;
import com.udacity.bakingapp.models.Recipe;
import com.udacity.bakingapp.models.RecipeStep;
import com.udacity.bakingapp.utilities.UserInterfaceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeStepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeStepListActivity extends AppCompatActivity implements RecipeStepDetailFragment.OnCompleteListener {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recipe_list) View mRecyclerView;

    public boolean mTwoPane;
    private Recipe mRecipe;
    public int mCurrentStepIndex;

    public static final String EXTRA_RECIPE = "EXTRA_RECIPE";
    public static final String EXTRA_RECIPE_CURRENT_STEP = "EXTRA_RECIPE_CURRENT_STEP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_step_list);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        if ((getResources().getBoolean(R.bool.isTablet)))
            mTwoPane = true;

        Intent intent = getIntent();

        if (savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable(EXTRA_RECIPE);
            mCurrentStepIndex = savedInstanceState.getInt(RecipeStepListActivity.EXTRA_RECIPE_CURRENT_STEP, 0);
        }
        else if (intent != null)
            mRecipe = intent.getParcelableExtra(EXTRA_RECIPE);

        if (mRecipe == null)
            closeOnError();

        setTitle(mRecipe.name);

        if (savedInstanceState == null && mTwoPane) {
            Bundle arguments = new Bundle();
            RecipeStep ingredientsAsRecipeStep = new RecipeStep(
                    (long)0,
                    getResources().getString(R.string.ingredients), mRecipe.getIngredientsAsString(),
                    null,
                    mRecipe.image);
            arguments.putParcelable(RecipeStepDetailFragment.ARG_RECIPE_STEP, ingredientsAsRecipeStep);
            RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_detail_container, fragment)
                    .commit();
        }

        assert mRecyclerView != null;
        setupRecyclerView((RecyclerView) mRecyclerView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_RECIPE, mRecipe);
        outState.putInt(EXTRA_RECIPE_CURRENT_STEP, mCurrentStepIndex);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void closeOnError() {
        UserInterfaceUtils.ShowToastMessage(getString(R.string.recipe_error_message), this);
        finish();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new RecipeStepListRecyclerViewAdapter(this, mRecipe, mTwoPane));
    }

    @Override
    public void onFragmentAttachComplete() {
        Button buttonPrevious = findViewById(R.id.buttonPrevious);
        Button buttonNext = findViewById(R.id.buttonNext);

        buttonPrevious.setVisibility(View.GONE);
        buttonNext.setVisibility(View.GONE);
    }
}
