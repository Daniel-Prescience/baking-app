package com.udacity.bakingapp.activities;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import com.udacity.bakingapp.R;
import com.udacity.bakingapp.fragments.RecipeStepDetailFragment;
import com.udacity.bakingapp.models.Recipe;
import com.udacity.bakingapp.models.RecipeStep;
import com.udacity.bakingapp.utilities.UserInterfaceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a single Recipe detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeStepListActivity}.
 */
public class RecipeStepDetailActivity extends AppCompatActivity implements RecipeStepDetailFragment.OnCompleteListener {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.detail_toolbar) Toolbar mToolbar;

    private int mCurrentStepIndex;
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_step_detail);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        mRecipe = getIntent().getParcelableExtra(RecipeStepListActivity.EXTRA_RECIPE);
        mCurrentStepIndex = getIntent().getIntExtra(RecipeStepListActivity.EXTRA_RECIPE_CURRENT_STEP, 0);

        if (mRecipe == null || mCurrentStepIndex < 0 || mCurrentStepIndex > mRecipe.steps.length)
            closeOnError();

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity using a fragment transaction.
            Bundle arguments = new Bundle();

            if (mCurrentStepIndex == 0) {
                setTitle(getResources().getString(R.string.ingredients));
                RecipeStep ingredientsAsRecipeStep = new RecipeStep(
                        (long) 0,
                        getResources().getString(R.string.ingredients),
                        mRecipe.getIngredientsAsString(),
                        null,
                        mRecipe.image);
                arguments.putParcelable(RecipeStepDetailFragment.ARG_RECIPE_STEP, ingredientsAsRecipeStep);
            }
            else {
                setTitle(mRecipe.steps[mCurrentStepIndex - 1].shortDescription);
                arguments.putParcelable(RecipeStepDetailFragment.ARG_RECIPE_STEP, mRecipe.steps[mCurrentStepIndex - 1]);
            }

            RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_container, fragment)
                    .commit();
        }
        else
            mCurrentStepIndex = savedInstanceState.getInt(RecipeStepListActivity.EXTRA_RECIPE_CURRENT_STEP, 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(RecipeStepListActivity.EXTRA_RECIPE_CURRENT_STEP, mCurrentStepIndex);
    }

    private void closeOnError() {
        UserInterfaceUtils.ShowToastMessage(getString(R.string.recipe_error_message), this);
        finish();
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

    @Override
    public void onFragmentAttachComplete() {
        Button buttonPrevious = findViewById(R.id.buttonPrevious);
        Button buttonNext = findViewById(R.id.buttonNext);

        buttonPrevious.setVisibility(View.VISIBLE);
        buttonNext.setVisibility(View.VISIBLE);

        ScrollView scrollRecipeStepDetail = findViewById(R.id.recipe_step_detail_scroll);
        ConstraintLayout constraintLayout  = findViewById(R.id.playerLayout);
        ConstraintSet set = new ConstraintSet();

        set.clone(constraintLayout);
        set.constrainHeight(scrollRecipeStepDetail.getId(), ConstraintSet.MATCH_CONSTRAINT);
        set.connect(scrollRecipeStepDetail.getId(), ConstraintSet.BOTTOM, buttonPrevious.getId(), ConstraintSet.TOP);
        set.applyTo(constraintLayout);

        if (mCurrentStepIndex == 0)
            setTitle(getResources().getString(R.string.ingredients));
        else
            setTitle(mRecipe.steps[mCurrentStepIndex - 1].shortDescription);

        if (mCurrentStepIndex > 0) {
            buttonPrevious.setEnabled(true);
            buttonPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle arguments = new Bundle();
                    mCurrentStepIndex--;
                    if (mCurrentStepIndex == 0) {
                        RecipeStep ingredientsAsRecipeStep = new RecipeStep(
                                (long) 0,
                                getResources().getString(R.string.ingredients), mRecipe.getIngredientsAsString(),
                                null,
                                mRecipe.image);
                        arguments.putParcelable(RecipeStepDetailFragment.ARG_RECIPE_STEP, ingredientsAsRecipeStep);
                    }
                    else
                        arguments.putParcelable(RecipeStepDetailFragment.ARG_RECIPE_STEP, mRecipe.steps[mCurrentStepIndex - 1]);

                    RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recipe_detail_container, fragment)
                            .commit();
                }
            });
        }
        else
            buttonPrevious.setEnabled(false);

        if (mCurrentStepIndex < mRecipe.steps.length) {
            buttonNext.setEnabled(true);
            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(RecipeStepDetailFragment.ARG_RECIPE_STEP, mRecipe.steps[++mCurrentStepIndex - 1]);
                    RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recipe_detail_container, fragment)
                            .commit();
                }
            });
        }
        else
            buttonNext.setEnabled(false);
    }
}
